package org.zstack.test.storage.snapshot;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.componentloader.ComponentLoader;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.SimpleQuery;
import org.zstack.core.db.SimpleQuery.Op;
import org.zstack.header.identity.SessionInventory;
import org.zstack.header.query.QueryOp;
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

/*
* 1. take 4 snapshot from vm's root volume
* 2. backup snapshot 4
*
* query using nested query
*
*/
public class TestQuerySnapshot1 {
    CLogger logger = Utils.getLogger(TestQuerySnapshot1.class);
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
        deployer = new Deployer("deployerXml/kvm/TestCreateVmOnKvm.xml", con);
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
        BackupStorageInventory bs = deployer.backupStorages.get("sftp");
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

        inv3 = api.backupSnapshot(inv3.getUuid());
        Assert.assertEquals(bs.getUuid(), inv3.getBackupStorageRefs().get(0).getBackupStorageUuid());
        Assert.assertNotNull(inv3.getBackupStorageRefs().get(0).getInstallPath());

        SimpleQuery<VolumeSnapshotBackupStorageRefVO> q = dbf.createQuery(VolumeSnapshotBackupStorageRefVO.class);
        q.add(VolumeSnapshotBackupStorageRefVO_.backupStorageUuid, SimpleQuery.Op.NOT_NULL);
        long count = q.count();
        Assert.assertEquals(4, count);

        q = dbf.createQuery(VolumeSnapshotBackupStorageRefVO.class);
        q.add(VolumeSnapshotBackupStorageRefVO_.installPath, SimpleQuery.Op.NOT_NULL);
        count = q.count();
        Assert.assertEquals(4, count);

        VolumeSnapshotVO vo3 = dbf.findByUuid(inv3.getUuid(), VolumeSnapshotVO.class);
        SimpleQuery<VolumeSnapshotBackupStorageRefVO> refq = dbf.createQuery(VolumeSnapshotBackupStorageRefVO.class);
        refq.add(VolumeSnapshotBackupStorageRefVO_.volumeSnapshotUuid, Op.EQ, vo3.getUuid());
        VolumeSnapshotBackupStorageRefVO refvo = refq.find();

        APIQueryVolumeSnapshotMsg msg = new APIQueryVolumeSnapshotMsg();
        msg.addQueryCondition("backupStorageRefs.installPath", QueryOp.EQ, refvo.getInstallPath());
        APIQueryVolumeSnapshotReply reply = api.query(msg, APIQueryVolumeSnapshotReply.class);
        VolumeSnapshotInventory sinv = reply.getInventories().get(0);
        Assert.assertEquals(refvo.getVolumeSnapshotUuid(), sinv.getUuid());

        msg = new APIQueryVolumeSnapshotMsg();
        msg.addQueryCondition("backupStorageRefs.installPath", QueryOp.EQ, refvo.getInstallPath());
        msg.addQueryCondition("backupStorageRefs.volumeSnapshotUuid", QueryOp.EQ, inv2.getUuid());
        reply = api.query(msg, APIQueryVolumeSnapshotReply.class);
        Assert.assertTrue(reply.getInventories().isEmpty());
    }
}
