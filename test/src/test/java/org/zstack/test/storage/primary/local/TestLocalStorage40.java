package org.zstack.test.storage.primary.local;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.componentloader.ComponentLoader;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.header.configuration.DiskOfferingInventory;
import org.zstack.header.identity.SessionInventory;
import org.zstack.header.storage.primary.PrimaryStorageInventory;
import org.zstack.header.vm.VmInstanceInventory;
import org.zstack.header.volume.VolumeInventory;
import org.zstack.storage.primary.local.LocalStorageSimulatorConfig;
import org.zstack.storage.primary.local.LocalStorageSimulatorConfig.Capacity;
import org.zstack.test.Api;
import org.zstack.test.ApiSenderException;
import org.zstack.test.DBUtil;
import org.zstack.test.WebBeanConstructor;
import org.zstack.test.deployer.Deployer;
import org.zstack.utils.data.SizeUnit;

import java.util.List;

/**
 * 1. create two vms on different hosts
 * 2. attach a data volume to the vm1
 * 3. detach the data volume, now the data volume is ready to attach
 * <p>
 * confirm the candidate vm for attaching data volume is vm1
 * confirm the vm1's candidate volume is the data volume
 * <p>
 * 4. migrate the data volume to vm2's host
 * <p>
 * confirm the candidate vm for attaching data volume is vm2
 * confirm the vm2's candidate volume is the data volume
 * <p>
 * 5. migrate the data volume to vm1' host
 * <p>
 * confirm the candidate vm for attaching data volume is vm1
 * confirm the vm1's candidate volume is the data volume
 */
public class TestLocalStorage40 {
    Deployer deployer;
    Api api;
    ComponentLoader loader;
    CloudBus bus;
    DatabaseFacade dbf;
    SessionInventory session;
    LocalStorageSimulatorConfig config;
    long totalSize = SizeUnit.GIGABYTE.toByte(100);

    @Before
    public void setUp() throws Exception {
        DBUtil.reDeployDB();
        WebBeanConstructor con = new WebBeanConstructor();
        deployer = new Deployer("deployerXml/localStorage/TestLocalStorage11.xml", con);
        deployer.addSpringConfig("KVMRelated.xml");
        deployer.addSpringConfig("localStorageSimulator.xml");
        deployer.addSpringConfig("localStorage.xml");
        deployer.load();

        loader = deployer.getComponentLoader();
        bus = loader.getComponent(CloudBus.class);
        dbf = loader.getComponent(DatabaseFacade.class);
        config = loader.getComponent(LocalStorageSimulatorConfig.class);

        Capacity c = new Capacity();
        c.total = totalSize;
        c.avail = totalSize;

        config.capacityMap.put("host1", c);
        config.capacityMap.put("host2", c);

        deployer.build();
        api = deployer.getApi();
        session = api.loginAsAdmin();
    }

    @Test
    public void test() throws ApiSenderException {
        PrimaryStorageInventory local = deployer.primaryStorages.get("local");
        VmInstanceInventory vm1 = deployer.vms.get("TestVm");
        VmInstanceInventory vm2 = deployer.vms.get("TestVm1");
        DiskOfferingInventory dof = deployer.diskOfferings.get("TestDiskOffering1");
        VolumeInventory data = api.createDataVolume("data", dof.getUuid());
        data = api.attachVolumeToVm(vm1.getUuid(), data.getUuid());
        data = api.detachVolumeFromVm(data.getUuid());

        List<VmInstanceInventory> vms = api.getDataVolumeCandidateVmForAttaching(data.getUuid());
        Assert.assertEquals(1, vms.size());

        api.localStorageMigrateVolume(data.getUuid(), vm2.getHostUuid(), null);

        vms = api.getDataVolumeCandidateVmForAttaching(data.getUuid());
        Assert.assertEquals(1, vms.size());
        Assert.assertEquals(vm2.getUuid(), vms.get(0).getUuid());

        List<VolumeInventory> vols = api.getVmAttachableVolume(vm2.getUuid());
        Assert.assertEquals(1, vols.size());
        Assert.assertEquals(data.getUuid(), vols.get(0).getUuid());

        vols = api.getVmAttachableVolume(vm1.getUuid());
        Assert.assertEquals(0, vols.size());

        api.localStorageMigrateVolume(data.getUuid(), vm1.getHostUuid(), null);

        vols = api.getVmAttachableVolume(vm1.getUuid());
        Assert.assertEquals(1, vols.size());
        Assert.assertEquals(data.getUuid(), vols.get(0).getUuid());

        vols = api.getVmAttachableVolume(vm2.getUuid());
        Assert.assertEquals(0, vols.size());
    }
}
