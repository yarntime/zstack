package org.zstack.test.diskcapacity;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.zstack.core.Platform;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.componentloader.ComponentLoader;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.header.configuration.InstanceOfferingInventory;
import org.zstack.header.host.HostInventory;
import org.zstack.header.identity.SessionInventory;
import org.zstack.header.image.APIAddImageEvent;
import org.zstack.header.image.APIAddImageMsg;
import org.zstack.header.image.ImageInventory;
import org.zstack.header.message.AbstractBeforeDeliveryMessageInterceptor;
import org.zstack.header.message.Message;
import org.zstack.header.network.l3.L3NetworkInventory;
import org.zstack.header.storage.backup.BackupStorageInventory;
import org.zstack.header.storage.backup.BackupStorageVO;
import org.zstack.header.storage.primary.InstantiateVolumeOnPrimaryStorageMsg;
import org.zstack.header.storage.primary.PrimaryStorageCapacityVO;
import org.zstack.header.storage.primary.PrimaryStorageInventory;
import org.zstack.header.storage.snapshot.VolumeSnapshotInventory;
import org.zstack.header.vm.VmInstanceInventory;
import org.zstack.header.volume.VolumeInventory;
import org.zstack.header.volume.VolumeType;
import org.zstack.simulator.kvm.KVMSimulatorConfig;
import org.zstack.simulator.storage.backup.sftp.SftpBackupStorageSimulatorConfig;
import org.zstack.storage.primary.local.LocalStorageHostRefVO;
import org.zstack.storage.primary.local.LocalStorageHostRefVOFinder;
import org.zstack.storage.primary.local.LocalStorageSimulatorConfig;
import org.zstack.storage.primary.local.LocalStorageSimulatorConfig.Capacity;
import org.zstack.test.*;
import org.zstack.test.deployer.Deployer;
import org.zstack.test.storage.backup.sftp.TestSftpBackupStorageDeleteImage2;
import org.zstack.utils.Utils;
import org.zstack.utils.data.SizeUnit;
import org.zstack.utils.logging.CLogger;

import java.util.ArrayList;
import java.util.List;

import static org.zstack.utils.CollectionDSL.list;

/**
 * 1. use local storage
 * 2. add an image
 * 3. create a vm from the image
 * 4. create 50 snapshots from the root volume
 * 5. create a template from a snapshot
 * 6. create a data volume from a snapshot
 * <p>
 * confirm the size of snapshots correct
 * confirm the local storage capacity correct
 * confirm the capacity of the backup storage correct
 */
public class TestDiskCapacityLocalStorage4 {
    CLogger logger = Utils.getLogger(TestSftpBackupStorageDeleteImage2.class);
    Deployer deployer;
    Api api;
    ComponentLoader loader;
    CloudBus bus;
    DatabaseFacade dbf;
    SessionInventory session;
    SftpBackupStorageSimulatorConfig sconfig;
    LocalStorageSimulatorConfig lconfig;
    KVMSimulatorConfig kconfig;

    @Before
    public void setUp() throws Exception {
        DBUtil.reDeployDB();
        WebBeanConstructor con = new WebBeanConstructor();
        deployer = new Deployer("deployerXml/diskcapacity/TestDiskCapacityLocalStorage1.xml", con);
        deployer.addSpringConfig("KVMRelated.xml");
        deployer.addSpringConfig("localStorageSimulator.xml");
        deployer.addSpringConfig("localStorage.xml");
        deployer.load();

        api = deployer.getApi();
        loader = deployer.getComponentLoader();
        bus = loader.getComponent(CloudBus.class);
        dbf = loader.getComponent(DatabaseFacade.class);
        sconfig = loader.getComponent(SftpBackupStorageSimulatorConfig.class);
        lconfig = loader.getComponent(LocalStorageSimulatorConfig.class);
        kconfig = loader.getComponent(KVMSimulatorConfig.class);

        Capacity c = new Capacity();
        c.total = SizeUnit.TERABYTE.toByte(10);
        c.avail = c.total;

        lconfig.capacityMap.put("host1", c);

        deployer.build();
        api.prepare();
        session = api.loginAsAdmin();
    }

    class AddImage {
        long size;
        long actualSize;
        String name = "image";
        BackupStorageInventory sftp = deployer.backupStorages.get("sftp");

        ImageInventory add() throws ApiSenderException {
            String uuid = Platform.getUuid();

            sconfig.imageSizes.put(uuid, size);
            sconfig.imageActualSizes.put(uuid, actualSize);

            APIAddImageMsg msg = new APIAddImageMsg();
            msg.setResourceUuid(uuid);
            msg.setName(name);
            msg.setBackupStorageUuids(list(sftp.getUuid()));
            msg.setFormat("qcow2");
            msg.setUrl("http://image.qcow2");
            msg.setSession(session);
            ApiSender sender = new ApiSender();
            APIAddImageEvent evt = sender.send(msg, APIAddImageEvent.class);
            return evt.getInventory();
        }
    }

    class CreateVm {
        VmCreator creator = new VmCreator(api);
        String name = "vm";
        String imageUuid;
        long rootVolumeActualSize;

        VmInstanceInventory create() throws ApiSenderException {
            InstanceOfferingInventory ioinv = deployer.instanceOfferings.get("TestInstanceOffering");
            L3NetworkInventory l3 = deployer.l3Networks.get("TestL3Network1");

            bus.installBeforeDeliveryMessageInterceptor(new AbstractBeforeDeliveryMessageInterceptor() {
                @Override
                public void intercept(Message msg) {
                    InstantiateVolumeOnPrimaryStorageMsg imsg = (InstantiateVolumeOnPrimaryStorageMsg) msg;
                    VolumeInventory vol = imsg.getVolume();
                    if (VolumeType.Root.toString().equals(vol.getType())) {
                        lconfig.getVolumeSizeCmdActualSize.put(vol.getUuid(), rootVolumeActualSize);
                    }
                }
            }, InstantiateVolumeOnPrimaryStorageMsg.class);

            creator.session = session;
            creator.name = name;
            creator.imageUuid = imageUuid;
            creator.addL3Network(l3.getUuid());
            creator.instanceOfferingUuid = ioinv.getUuid();
            return creator.create();
        }
    }

    class TakeSnapshot {
        String volumeUuid;
        long size;

        VolumeSnapshotInventory take() throws ApiSenderException {
            kconfig.takeSnapshotCmdSize.put(volumeUuid, size);
            return api.createSnapshot(volumeUuid);
        }
    }

    @Test
    public void test() throws ApiSenderException {
        AddImage addImage = new AddImage();
        addImage.size = SizeUnit.GIGABYTE.toByte(10);
        addImage.actualSize = SizeUnit.GIGABYTE.toByte(1);
        ImageInventory image = addImage.add();

        Assert.assertEquals(image.getSize(), addImage.size);
        Assert.assertEquals(image.getActualSize().longValue(), addImage.actualSize);

        CreateVm createVm = new CreateVm();
        createVm.imageUuid = image.getUuid();
        createVm.rootVolumeActualSize = addImage.actualSize;
        VmInstanceInventory vm = createVm.create();
        VolumeInventory root = vm.getRootVolume();

        int num = 30;

        List<VolumeSnapshotInventory> snapshots = new ArrayList<VolumeSnapshotInventory>();
        long snapshotSize = 0;
        for (int i = 1; i < num; i++) {
            TakeSnapshot takeSnapshot = new TakeSnapshot();
            takeSnapshot.size = SizeUnit.MEGABYTE.toByte(i);
            takeSnapshot.volumeUuid = root.getUuid();
            VolumeSnapshotInventory sp = takeSnapshot.take();
            Assert.assertEquals(takeSnapshot.size, sp.getSize());
            snapshotSize += takeSnapshot.size;
            snapshots.add(sp);
        }

        PrimaryStorageInventory local = deployer.primaryStorages.get("local");
        PrimaryStorageCapacityVO pscap = dbf.findByUuid(local.getUuid(), PrimaryStorageCapacityVO.class);

        HostInventory host = deployer.hosts.get("host1");
        LocalStorageHostRefVO href = new LocalStorageHostRefVOFinder().findByPrimaryKey(host.getUuid(), local.getUuid());

        // image cache + volume + snapshot
        long used = addImage.actualSize + root.getSize() + snapshotSize;
        long avail = pscap.getTotalCapacity() - used;
        Assert.assertEquals(avail, pscap.getAvailableCapacity());
        Assert.assertEquals(avail, href.getAvailableCapacity());

        BackupStorageInventory bs = deployer.backupStorages.get("sftp");
        BackupStorageVO bsBefore = dbf.findByUuid(bs.getUuid(), BackupStorageVO.class);
        VolumeSnapshotInventory sp = snapshots.get(0);
        long spToTemplateActualSize = SizeUnit.GIGABYTE.toByte(10);

        lconfig.snapshotToVolumeSize.put(sp.getVolumeUuid(), root.getSize());
        lconfig.snapshotToVolumeActualSize.put(sp.getVolumeUuid(), spToTemplateActualSize);
        ImageInventory template = api.createTemplateFromSnapshot(sp.getUuid(), bs.getUuid());
        Assert.assertEquals(root.getSize(), template.getSize());
        Assert.assertEquals(spToTemplateActualSize, template.getActualSize().longValue());

        BackupStorageVO bsAfter = dbf.findByUuid(bs.getUuid(), BackupStorageVO.class);
        Assert.assertEquals(spToTemplateActualSize, bsBefore.getAvailableCapacity() - bsAfter.getAvailableCapacity());

        long dataVolumeActualSize = SizeUnit.GIGABYTE.toByte(2);
        long dataVolumeSize = SizeUnit.GIGABYTE.toByte(20);
        lconfig.snapshotToVolumeSize.put(sp.getVolumeUuid(), dataVolumeSize);
        lconfig.snapshotToVolumeActualSize.put(sp.getVolumeUuid(), dataVolumeActualSize);
        // create a data volume from the snapshot
        VolumeInventory vol = api.createDataVolumeFromSnapshot(sp.getUuid());
        Assert.assertEquals(dataVolumeActualSize, vol.getActualSize().longValue());
        Assert.assertEquals(dataVolumeSize, vol.getSize());

        avail = avail - dataVolumeSize;
        pscap = dbf.findByUuid(local.getUuid(), PrimaryStorageCapacityVO.class);
        href = new LocalStorageHostRefVOFinder().findByPrimaryKey(host.getUuid(), local.getUuid());
        Assert.assertEquals(avail, pscap.getAvailableCapacity());
        Assert.assertEquals(avail, href.getAvailableCapacity());
    }
}
