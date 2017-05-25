package org.zstack.test.storage.primary.local;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.zstack.compute.host.HostGlobalConfig;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.componentloader.ComponentLoader;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.header.cluster.ClusterInventory;
import org.zstack.header.host.APIAddHostEvent;
import org.zstack.header.host.HostInventory;
import org.zstack.header.host.HostStatus;
import org.zstack.header.host.HostVO;
import org.zstack.header.identity.SessionInventory;
import org.zstack.header.storage.primary.PrimaryStorageInventory;
import org.zstack.header.storage.primary.PrimaryStorageVO;
import org.zstack.kvm.APIAddKVMHostMsg;
import org.zstack.storage.primary.local.LocalStorageHostRefVO;
import org.zstack.storage.primary.local.LocalStorageHostRefVOFinder;
import org.zstack.storage.primary.local.LocalStorageSimulatorConfig;
import org.zstack.storage.primary.local.LocalStorageSimulatorConfig.Capacity;
import org.zstack.test.*;
import org.zstack.test.deployer.Deployer;
import org.zstack.utils.data.SizeUnit;

import java.util.concurrent.TimeUnit;

/**
 * 1. use local storage
 * 3. add a host
 * 4. make the host disconnected
 * 5. wait for host reconnecting
 * <p>
 * confirm all local storage related commands, VOs are set
 */
public class TestLocalStorage6 {
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
        deployer = new Deployer("deployerXml/localStorage/TestLocalStorage6.xml", con);
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

        deployer.build();
        api = deployer.getApi();
        session = api.loginAsAdmin();
    }

    @Test
    public void test() throws ApiSenderException, InterruptedException {
        ClusterInventory cluster = deployer.clusters.get("Cluster1");
        PrimaryStorageInventory local = deployer.primaryStorages.get("local");
        PrimaryStorageInventory local2 = deployer.primaryStorages.get("local2");


        APIAddKVMHostMsg msg = new APIAddKVMHostMsg();
        msg.setName("host1");
        msg.setClusterUuid(cluster.getUuid());
        msg.setManagementIp("127.0.0.1");
        msg.setSession(api.getAdminSession());
        msg.setUsername("root");
        msg.setPassword("password");
        ApiSender sender = api.getApiSender();
        APIAddHostEvent evt = sender.send(msg, APIAddHostEvent.class);
        HostInventory host1 = evt.getInventory();


        HostVO hvo1 = dbf.findByUuid(host1.getUuid(), HostVO.class);
        hvo1.setStatus(HostStatus.Disconnected);
        dbf.update(hvo1);
        HostGlobalConfig.PING_HOST_INTERVAL.updateValue(1);

        TimeUnit.SECONDS.sleep(5);

        LocalStorageHostRefVO href = new LocalStorageHostRefVOFinder().findByPrimaryKey(host1.getUuid(), local.getUuid());
        PrimaryStorageVO lvo = dbf.findByUuid(local.getUuid(), PrimaryStorageVO.class);
        if (href == null) {
            href = new LocalStorageHostRefVOFinder().findByPrimaryKey(host1.getUuid(), local2.getUuid());
            lvo = dbf.findByUuid(local2.getUuid(), PrimaryStorageVO.class);
        }
        Assert.assertEquals(href.getTotalCapacity(), totalSize);
        Assert.assertEquals(href.getTotalPhysicalCapacity(), totalSize);


        Assert.assertEquals(totalSize, lvo.getCapacity().getTotalCapacity());
        Assert.assertEquals(totalSize, lvo.getCapacity().getTotalPhysicalCapacity());
    }
}
