package org.zstack.test.storage.snapshot;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.componentloader.ComponentLoader;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.SimpleQuery;
import org.zstack.core.db.SimpleQuery.Op;
import org.zstack.header.identity.SessionInventory;
import org.zstack.header.image.ImageConstant.ImageMediaType;
import org.zstack.header.image.ImageInventory;
import org.zstack.header.storage.backup.BackupStorageInventory;
import org.zstack.header.storage.snapshot.*;
import org.zstack.header.vm.VmInstanceInventory;
import org.zstack.header.volume.VolumeVO;
import org.zstack.simulator.storage.primary.nfs.NfsPrimaryStorageSimulatorConfig;
import org.zstack.test.Api;
import org.zstack.test.ApiSenderException;
import org.zstack.test.DBUtil;
import org.zstack.test.WebBeanConstructor;
import org.zstack.test.deployer.Deployer;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

import javax.persistence.Query;

/*
* 1. take 4 snapshot from vm's root volume
* 2. backup snapshots
* 3. fake snapshots are destroyed on primary storage
* 4. create a new template from snapshot 4
*
* confirm template is created on sftp1
*/
@Deprecated
public class TestSnapshotOnKvm25 {
    CLogger logger = Utils.getLogger(TestSnapshotOnKvm25.class);
    Deployer deployer;
    Api api;
    ComponentLoader loader;
    CloudBus bus;
    DatabaseFacade dbf;
    SessionInventory session;
    NfsPrimaryStorageSimulatorConfig nfsConfig;

    @Before
    public void setUp() throws Exception {
        DBUtil.reDeployDB();
        WebBeanConstructor con = new WebBeanConstructor();
        deployer = new Deployer("deployerXml/kvm/TestTakeSnapshotOnKvm12.xml", con);
        deployer.addSpringConfig("KVMRelated.xml");
        deployer.build();
        api = deployer.getApi();
        loader = deployer.getComponentLoader();
        bus = loader.getComponent(CloudBus.class);
        dbf = loader.getComponent(DatabaseFacade.class);
        nfsConfig = loader.getComponent(NfsPrimaryStorageSimulatorConfig.class);
        session = api.loginAsAdmin();
    }

    private void fullSnapshot(VolumeSnapshotInventory inv, int distance) {
        Assert.assertEquals(VolumeSnapshotState.Enabled.toString(), inv.getState());
        Assert.assertEquals(VolumeSnapshotStatus.Ready.toString(), inv.getStatus());
        VolumeVO vol = dbf.findByUuid(inv.getVolumeUuid(), VolumeVO.class);
        VolumeSnapshotVO svo = dbf.findByUuid(inv.getUuid(), VolumeSnapshotVO.class);
        Assert.assertNotNull(svo);
        Assert.assertFalse(svo.isFullSnapshot());
        Assert.assertTrue(svo.isLatest());
        Assert.assertNull(svo.getParentUuid());
        Assert.assertEquals(distance, svo.getDistance());
        Assert.assertEquals(vol.getPrimaryStorageUuid(), svo.getPrimaryStorageUuid());
        Assert.assertNotNull(svo.getPrimaryStorageInstallPath());
        VolumeSnapshotTreeVO cvo = dbf.findByUuid(svo.getTreeUuid(), VolumeSnapshotTreeVO.class);
        Assert.assertNotNull(cvo);
        Assert.assertTrue(cvo.isCurrent());
    }

    private void deltaSnapshot(VolumeSnapshotInventory inv, int distance) {
        Assert.assertEquals(VolumeSnapshotState.Enabled.toString(), inv.getState());
        Assert.assertEquals(VolumeSnapshotStatus.Ready.toString(), inv.getStatus());
        VolumeVO vol = dbf.findByUuid(inv.getVolumeUuid(), VolumeVO.class);
        VolumeSnapshotVO svo = dbf.findByUuid(inv.getUuid(), VolumeSnapshotVO.class);
        Assert.assertNotNull(svo);
        Assert.assertFalse(svo.isFullSnapshot());
        Assert.assertTrue(svo.isLatest());
        Assert.assertNotNull(svo.getParentUuid());
        Assert.assertEquals(distance, svo.getDistance());
        Assert.assertEquals(vol.getPrimaryStorageUuid(), svo.getPrimaryStorageUuid());
        Assert.assertNotNull(svo.getPrimaryStorageInstallPath());
        VolumeSnapshotTreeVO cvo = dbf.findByUuid(svo.getTreeUuid(), VolumeSnapshotTreeVO.class);
        Assert.assertNotNull(cvo);
        Assert.assertTrue(cvo.isCurrent());
        Assert.assertEquals(svo.getTreeUuid(), cvo.getUuid());
    }

    @Test
    public void test() throws ApiSenderException, InterruptedException {
        BackupStorageInventory bs = deployer.backupStorages.get("sftp1");
        VmInstanceInventory vm = deployer.vms.get("TestVm");
        String volUuid = vm.getRootVolumeUuid();
        VolumeSnapshotInventory inv = api.createSnapshot(volUuid);
        fullSnapshot(inv, 0);

        inv = api.createSnapshot(volUuid);
        deltaSnapshot(inv, 1);

        VolumeSnapshotInventory inv2 = api.createSnapshot(volUuid);
        deltaSnapshot(inv2, 2);

        VolumeSnapshotInventory inv3 = api.createSnapshot(volUuid);
        deltaSnapshot(inv3, 3);

        api.backupSnapshot(inv3.getUuid());

        new Runnable() {
            @Override
            @Transactional
            public void run() {
                String sql = "update VolumeSnapshotVO s set s.primaryStorageUuid = null, s.primaryStorageInstallPath = null";
                Query q = dbf.getEntityManager().createQuery(sql);
                q.executeUpdate();
            }
        }.run();

        ImageInventory img = api.createTemplateFromSnapshot(inv3.getUuid(), bs.getUuid());
        Assert.assertNotNull(img.getBackupStorageRefs().get(0).getInstallPath());
        Assert.assertTrue(img.getSize() != 0);
        Assert.assertEquals(bs.getUuid(), img.getBackupStorageRefs().get(0).getBackupStorageUuid());
        Assert.assertEquals(ImageMediaType.RootVolumeTemplate.toString(), img.getMediaType());
        Assert.assertFalse(nfsConfig.rebaseAndMergeSnapshotsCmds.isEmpty());
        Assert.assertFalse(nfsConfig.uploadToSftpCmds.isEmpty());

        SimpleQuery<VolumeSnapshotVO> q = dbf.createQuery(VolumeSnapshotVO.class);
        q.add(VolumeSnapshotVO_.status, Op.EQ, VolumeSnapshotStatus.Ready);
        long count = q.count();
        Assert.assertEquals(4, count);
        // deleted temporary downloaded snapshots
        Assert.assertFalse(nfsConfig.deleteCmds.isEmpty());
    }
}
