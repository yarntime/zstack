package org.zstack.test.cascade;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.componentloader.ComponentLoader;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.SimpleQuery;
import org.zstack.header.cluster.ClusterVO;
import org.zstack.header.configuration.DiskOfferingInventory;
import org.zstack.header.configuration.DiskOfferingVO;
import org.zstack.header.configuration.InstanceOfferingInventory;
import org.zstack.header.configuration.InstanceOfferingVO;
import org.zstack.header.host.HostVO;
import org.zstack.header.image.ImageVO;
import org.zstack.header.network.l2.L2NetworkVO;
import org.zstack.header.network.l3.IpRangeVO;
import org.zstack.header.network.l3.IpRangeVO_;
import org.zstack.header.network.l3.L3NetworkVO;
import org.zstack.header.storage.backup.BackupStorageInventory;
import org.zstack.header.storage.backup.BackupStorageVO;
import org.zstack.header.storage.primary.PrimaryStorageVO;
import org.zstack.header.vm.VmInstanceInventory;
import org.zstack.header.vm.VmInstanceState;
import org.zstack.header.vm.VmInstanceVO;
import org.zstack.header.zone.ZoneVO;
import org.zstack.test.Api;
import org.zstack.test.ApiSenderException;
import org.zstack.test.DBUtil;
import org.zstack.test.deployer.Deployer;

/**
 * delete ip range, start vm
 */
public class TestCascadeDeletion11 {
    Deployer deployer;
    Api api;
    ComponentLoader loader;
    CloudBus bus;
    DatabaseFacade dbf;

    @Before
    public void setUp() throws Exception {
        DBUtil.reDeployDB();
        deployer = new Deployer("deployerXml/vm/TestCreateVm.xml");
        deployer.build();
        api = deployer.getApi();
        loader = deployer.getComponentLoader();
        bus = loader.getComponent(CloudBus.class);
        dbf = loader.getComponent(DatabaseFacade.class);
    }

    @Test(expected = ApiSenderException.class)
    public void test() throws ApiSenderException, InterruptedException {
        SimpleQuery<IpRangeVO> q = dbf.createQuery(IpRangeVO.class);
        q.add(IpRangeVO_.name, SimpleQuery.Op.EQ, "TestIpRange1");
        IpRangeVO ipr = q.find();
        VmInstanceInventory vm = deployer.vms.get("TestVm");
        DiskOfferingInventory do1 = deployer.diskOfferings.get("TestRootDiskOffering");
        DiskOfferingInventory do2 = deployer.diskOfferings.get("TestDataDiskOffering");
        InstanceOfferingInventory io = deployer.instanceOfferings.get("TestInstanceOffering");
        BackupStorageInventory bs = deployer.backupStorages.get("TestBackupStorage");

        api.deleteIpRange(ipr.getUuid());
        long count = dbf.count(ZoneVO.class);
        Assert.assertTrue(0 != count);
        count = dbf.count(ClusterVO.class);
        Assert.assertTrue(0 != count);
        count = dbf.count(HostVO.class);
        Assert.assertTrue(0 != count);
        count = dbf.count(VmInstanceVO.class);
        Assert.assertTrue(0 != count);
        count = dbf.count(PrimaryStorageVO.class);
        Assert.assertTrue(0 != count);
        count = dbf.count(L2NetworkVO.class);
        Assert.assertTrue(0 != count);
        count = dbf.count(L3NetworkVO.class);
        Assert.assertEquals(3, count);
        count = dbf.count(IpRangeVO.class);
        Assert.assertEquals(2, count);
        count = dbf.count(ImageVO.class);
        Assert.assertTrue(0 != count);
        DiskOfferingVO dvo = dbf.findByUuid(do1.getUuid(), DiskOfferingVO.class);
        Assert.assertNotNull(dvo);
        dvo = dbf.findByUuid(do2.getUuid(), DiskOfferingVO.class);
        Assert.assertNotNull(dvo);
        InstanceOfferingVO ivo = dbf.findByUuid(io.getUuid(), InstanceOfferingVO.class);
        Assert.assertNotNull(ivo);
        BackupStorageVO bvo = dbf.findByUuid(bs.getUuid(), BackupStorageVO.class);
        Assert.assertNotNull(bvo);
        VmInstanceVO vmvo = dbf.findByUuid(vm.getUuid(), VmInstanceVO.class);
        Assert.assertEquals(VmInstanceState.Stopped, vmvo.getState());

        api.startVmInstance(vm.getUuid());
    }
}
