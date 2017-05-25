package org.zstack.test.kvm;

import org.junit.BeforeClass;
import org.junit.Test;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.componentloader.ComponentLoader;
import org.zstack.core.config.GlobalConfigFacade;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.header.cluster.ClusterInventory;
import org.zstack.header.host.APIAddHostEvent;
import org.zstack.header.host.HostInventory;
import org.zstack.header.identity.SessionInventory;
import org.zstack.header.storage.primary.APIAddPrimaryStorageEvent;
import org.zstack.header.storage.primary.PrimaryStorageInventory;
import org.zstack.header.zone.ZoneInventory;
import org.zstack.kvm.APIAddKVMHostMsg;
import org.zstack.kvm.KVMHostFactory;
import org.zstack.simulator.kvm.KVMSimulatorConfig;
import org.zstack.storage.primary.nfs.APIAddNfsPrimaryStorageMsg;
import org.zstack.storage.primary.nfs.NfsPrimaryStorageConstant;
import org.zstack.test.*;
import org.zstack.test.deployer.Deployer;
import org.zstack.utils.Utils;
import org.zstack.utils.data.SizeUnit;
import org.zstack.utils.logging.CLogger;

import java.util.List;

public class TestKvmNfsPrimaryStorageUnmount {
    static CLogger logger = Utils.getLogger(TestKvmNfsPrimaryStorageUnmount.class);
    static Deployer deployer;
    static Api api;
    static ComponentLoader loader;
    static CloudBus bus;
    static DatabaseFacade dbf;
    static KVMHostFactory kvmFactory;
    static SessionInventory session;
    static KVMSimulatorConfig config;
    static GlobalConfigFacade gcf;
    static ClusterInventory cinv;
    static WebBeanConstructor con;

    @BeforeClass
    public static void setUp() throws Exception {
        DBUtil.reDeployDB();
        con = new WebBeanConstructor();
        deployer = new Deployer("deployerXml/kvm/TestAddKvmHost.xml", con);
        deployer.addSpringConfig("KVMRelated.xml");
        deployer.build();
        api = deployer.getApi();
        loader = deployer.getComponentLoader();
        kvmFactory = loader.getComponent(KVMHostFactory.class);
        bus = loader.getComponent(CloudBus.class);
        dbf = loader.getComponent(DatabaseFacade.class);
        config = loader.getComponent(KVMSimulatorConfig.class);
        gcf = loader.getComponent(GlobalConfigFacade.class);
        session = api.loginAsAdmin();
        cinv = api.listClusters(null).get(0);
    }

    private void removeHostAndPrimaryStorage() throws ApiSenderException {
        List<PrimaryStorageInventory> pinvs = api.listPrimaryStorage(null);
        for (PrimaryStorageInventory pinv : pinvs) {
            api.detachPrimaryStorage(pinv.getUuid(), cinv.getUuid());
            api.deletePrimaryStorage(pinv.getUuid());
        }
        List<HostInventory> hinvs = api.listHosts(null);
        for (HostInventory hinv : hinvs) {
            api.deleteHost(hinv.getUuid());
        }
    }

    private HostInventory addHost() throws ApiSenderException {
        config.connectSuccess = true;
        config.connectException = false;
        config.hostFactSuccess = true;
        config.hostFactException = false;
        config.cpuNum = 1;
        config.cpuSpeed = 2600;
        config.totalMemory = SizeUnit.GIGABYTE.toByte(8);
        config.usedMemory = SizeUnit.MEGABYTE.toByte(512);
        config.usedCpu = 512;
        ClusterInventory cinv = api.listClusters(null).get(0);
        APIAddKVMHostMsg msg = new APIAddKVMHostMsg();
        msg.setName("KVM-1");
        msg.setClusterUuid(cinv.getUuid());
        msg.setManagementIp("localhost");
        msg.setUsername("admin");
        msg.setPassword("password");
        msg.setSession(session);
        ApiSender sender = api.getApiSender();
        APIAddHostEvent evt = sender.send(msg, APIAddHostEvent.class);
        return evt.getInventory();
    }

    private PrimaryStorageInventory addPrimaryStorage() throws ApiSenderException {
        config.totalDiskCapacity = SizeUnit.GIGABYTE.toByte(100);
        config.availableDiskCapacity = SizeUnit.GIGABYTE.toByte(50);
        config.mountException = false;
        config.mountSuccess = true;
        APIAddNfsPrimaryStorageMsg msg = new APIAddNfsPrimaryStorageMsg();
        msg.setName("KVM-NFS");
        msg.setUrl("10.1.1.22:/opt/zstack/");
        msg.setType(NfsPrimaryStorageConstant.NFS_PRIMARY_STORAGE_TYPE);
        msg.setSession(session);
        ZoneInventory zone = deployer.zones.get("Zone1");
        msg.setZoneUuid(zone.getUuid());
        ApiSender sender = api.getApiSender();
        APIAddPrimaryStorageEvent evt = sender.send(msg, APIAddPrimaryStorageEvent.class);
        PrimaryStorageInventory inv = evt.getInventory();
        api.attachPrimaryStorage(cinv.getUuid(), inv.getUuid());
        return inv;
    }


    @Test
    public void testDetach() throws ApiSenderException {
        addHost();
        addPrimaryStorage();
        config.unmountException = false;
        config.unmountSuccess = true;
        removeHostAndPrimaryStorage();
    }
}
