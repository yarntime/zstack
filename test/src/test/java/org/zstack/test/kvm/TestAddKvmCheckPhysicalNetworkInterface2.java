package org.zstack.test.kvm;

import org.junit.BeforeClass;
import org.junit.Test;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.componentloader.ComponentLoader;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.header.cluster.ClusterInventory;
import org.zstack.header.host.APIAddHostEvent;
import org.zstack.header.host.HostInventory;
import org.zstack.header.identity.SessionInventory;
import org.zstack.header.network.l2.L2NetworkInventory;
import org.zstack.kvm.APIAddKVMHostMsg;
import org.zstack.kvm.KVMHostFactory;
import org.zstack.simulator.kvm.KVMSimulatorConfig;
import org.zstack.test.*;
import org.zstack.test.deployer.Deployer;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

import java.util.List;

public class TestAddKvmCheckPhysicalNetworkInterface2 {
    static CLogger logger = Utils.getLogger(TestAddKvmHost.class);
    static Deployer deployer;
    static Api api;
    static ComponentLoader loader;
    static CloudBus bus;
    static DatabaseFacade dbf;
    static KVMHostFactory kvmFactory;
    static SessionInventory session;
    static KVMSimulatorConfig config;
    static ClusterInventory cinv;

    @BeforeClass
    public static void setUp() throws Exception {
        DBUtil.reDeployDB();
        WebBeanConstructor con = new WebBeanConstructor();
        deployer = new Deployer("deployerXml/kvm/TestAddKvmCheckPhysicalNetworkInterface2.xml", con);
        deployer.addSpringConfig("Kvm.xml");
        deployer.addSpringConfig("KVMSimulator.xml");
        deployer.build();
        api = deployer.getApi();
        loader = deployer.getComponentLoader();
        kvmFactory = loader.getComponent(KVMHostFactory.class);
        bus = loader.getComponent(CloudBus.class);
        dbf = loader.getComponent(DatabaseFacade.class);
        config = loader.getComponent(KVMSimulatorConfig.class);
        session = api.loginAsAdmin();
        cinv = api.listClusters(null).get(0);
    }

    private HostInventory addHost() throws ApiSenderException {
        config.connectSuccess = true;
        config.connectException = false;
        config.hostFactException = false;
        config.hostFactSuccess = true;
        APIAddKVMHostMsg msg = new APIAddKVMHostMsg();
        msg.setName("KVM-1");
        msg.setClusterUuid(cinv.getUuid());
        msg.setManagementIp("localhost");
        msg.setUsername("admin");
        msg.setPassword("password");
        msg.setSession(session);
        ApiSender sender = api.getApiSender();
        sender.setTimeout(10000);
        APIAddHostEvent evt = sender.send(msg, APIAddHostEvent.class);
        return evt.getInventory();
    }

    @Test
    public void test() throws ApiSenderException {
        config.checkPhysicalInterfaceSuccess = true;
        config.checkPhysicalInterfaceException = false;
        addHost();

        List<L2NetworkInventory> l2invs = api.listL2Network(null);
        for (L2NetworkInventory l2inv : l2invs) {
            api.attachL2NetworkToCluster(l2inv.getUuid(), cinv.getUuid());
        }
    }

}
