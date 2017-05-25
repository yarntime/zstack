package org.zstack.test.compute.hostallocator;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.componentloader.ComponentLoader;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.header.apimediator.ApiMediatorConstant;
import org.zstack.header.configuration.InstanceOfferingInventory;
import org.zstack.header.configuration.InstanceOfferingVO;
import org.zstack.header.image.ImageInventory;
import org.zstack.header.network.l3.L3NetworkInventory;
import org.zstack.header.storage.primary.PrimaryStorageInventory;
import org.zstack.header.storage.primary.PrimaryStorageVO;
import org.zstack.header.vm.APICreateVmInstanceEvent;
import org.zstack.header.vm.APICreateVmInstanceMsg;
import org.zstack.header.vm.VmInstanceConstant;
import org.zstack.header.vm.VmInstanceInventory;
import org.zstack.header.volume.VolumeInventory;
import org.zstack.header.volume.VolumeType;
import org.zstack.storage.primary.PrimaryStorageSystemTags;
import org.zstack.test.Api;
import org.zstack.test.ApiSender;
import org.zstack.test.ApiSenderException;
import org.zstack.test.DBUtil;
import org.zstack.test.deployer.Deployer;
import org.zstack.utils.CollectionUtils;
import org.zstack.utils.function.Function;

import java.util.ArrayList;
import java.util.List;

import static org.zstack.utils.CollectionDSL.e;
import static org.zstack.utils.CollectionDSL.map;

/**
 * 1. specify primary storage user tag as not required in instance offering
 * 2. create 3 vms
 * <p>
 * confirm 3 vms' root volume are on primary storage specified
 */
public class TestDesignatedHostAllocationStrategy8 {
    Deployer deployer;
    Api api;
    ComponentLoader loader;
    CloudBus bus;
    DatabaseFacade dbf;

    @Before
    public void setUp() throws Exception {
        DBUtil.reDeployDB();
        deployer = new Deployer("deployerXml/hostAllocator/TestHostAllocator2.xml");
        deployer.build();
        api = deployer.getApi();
        loader = deployer.getComponentLoader();
        bus = loader.getComponent(CloudBus.class);
        dbf = loader.getComponent(DatabaseFacade.class);
    }

    private class VmCreator {
        List<String> l3NetworkUuids = new ArrayList<String>();
        String imageUuid;
        String instanceOfferingUuid;
        List<String> diskOfferingUuids = new ArrayList<String>();
        String zoneUuid;
        String clusterUUid;
        String hostUuid;
        String name = "vm";

        void addL3Network(String uuid) {
            l3NetworkUuids.add(uuid);
        }

        void addDisk(String uuid) {
            diskOfferingUuids.add(uuid);
        }

        VmInstanceInventory create() throws ApiSenderException {
            APICreateVmInstanceMsg msg = new APICreateVmInstanceMsg();
            msg.setClusterUuid(clusterUUid);
            msg.setImageUuid(imageUuid);
            msg.setName(name);
            msg.setHostUuid(hostUuid);
            msg.setDataDiskOfferingUuids(diskOfferingUuids);
            msg.setInstanceOfferingUuid(instanceOfferingUuid);
            msg.setL3NetworkUuids(l3NetworkUuids);
            msg.setType(VmInstanceConstant.USER_VM_TYPE);
            msg.setZoneUuid(zoneUuid);
            msg.setHostUuid(hostUuid);
            msg.setServiceId(ApiMediatorConstant.SERVICE_ID);
            msg.setSession(api.getAdminSession());
            ApiSender sender = new ApiSender();
            APICreateVmInstanceEvent evt = sender.send(msg, APICreateVmInstanceEvent.class);
            return evt.getInventory();
        }
    }

    @Test
    public void test() throws ApiSenderException {
        L3NetworkInventory l3 = deployer.l3Networks.get("l3Network1");
        ImageInventory imageInventory = deployer.images.get("image1");
        InstanceOfferingInventory instanceOffering = deployer.instanceOfferings.get("instanceOffering512M512HZ");
        PrimaryStorageInventory ps1 = deployer.primaryStorages.get("primaryStorage1");
        api.createUserTag(ps1.getUuid(), "ps1", PrimaryStorageVO.class);
        String itag = PrimaryStorageSystemTags.PRIMARY_STORAGE_ALLOCATOR_USERTAG_TAG.instantiateTag(map(e("tag", "ps1")));
        api.createSystemTag(instanceOffering.getUuid(), itag, InstanceOfferingVO.class);

        VmCreator creator = new VmCreator();
        creator.addL3Network(l3.getUuid());
        creator.imageUuid = imageInventory.getUuid();
        creator.instanceOfferingUuid = instanceOffering.getUuid();
        VmInstanceInventory vm = creator.create();
        VolumeInventory root = CollectionUtils.find(vm.getAllVolumes(), new Function<VolumeInventory, VolumeInventory>() {
            @Override
            public VolumeInventory call(VolumeInventory arg) {
                return arg.getType().equals(VolumeType.Root.toString()) ? arg : null;
            }
        });

        Assert.assertEquals(ps1.getUuid(), root.getPrimaryStorageUuid());

        vm = creator.create();
        root = CollectionUtils.find(vm.getAllVolumes(), new Function<VolumeInventory, VolumeInventory>() {
            @Override
            public VolumeInventory call(VolumeInventory arg) {
                return arg.getType().equals(VolumeType.Root.toString()) ? arg : null;
            }
        });

        Assert.assertEquals(ps1.getUuid(), root.getPrimaryStorageUuid());

        vm = creator.create();
        root = CollectionUtils.find(vm.getAllVolumes(), new Function<VolumeInventory, VolumeInventory>() {
            @Override
            public VolumeInventory call(VolumeInventory arg) {
                return arg.getType().equals(VolumeType.Root.toString()) ? arg : null;
            }
        });

        Assert.assertEquals(ps1.getUuid(), root.getPrimaryStorageUuid());
    }
}
