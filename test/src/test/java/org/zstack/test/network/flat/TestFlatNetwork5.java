package org.zstack.test.network.flat;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.componentloader.ComponentLoader;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.header.apimediator.ApiMediatorConstant;
import org.zstack.header.host.HostInventory;
import org.zstack.header.identity.SessionInventory;
import org.zstack.header.network.l3.L3NetworkInventory;
import org.zstack.network.service.flat.*;
import org.zstack.storage.primary.local.LocalStorageSimulatorConfig;
import org.zstack.storage.primary.local.LocalStorageSimulatorConfig.Capacity;
import org.zstack.test.*;
import org.zstack.test.deployer.Deployer;
import org.zstack.utils.Utils;
import org.zstack.utils.data.SizeUnit;
import org.zstack.utils.logging.CLogger;

import java.util.concurrent.TimeUnit;

/**
 * 1. delete a flat network
 * <p>
 * confirm the namespace is deleted
 */
public class TestFlatNetwork5 {
    CLogger logger = Utils.getLogger(TestFlatNetwork5.class);
    Deployer deployer;
    Api api;
    ComponentLoader loader;
    CloudBus bus;
    DatabaseFacade dbf;
    SessionInventory session;
    LocalStorageSimulatorConfig config;
    FlatNetworkServiceSimulatorConfig fconfig;
    FlatDhcpUpgradeExtension dhcpUpgradeExtension;
    long totalSize = SizeUnit.GIGABYTE.toByte(100);

    @Before
    public void setUp() throws Exception {
        DBUtil.reDeployDB();
        WebBeanConstructor con = new WebBeanConstructor();
        deployer = new Deployer("deployerXml/flatnetwork/TestFlatNetwork2.xml", con);
        deployer.addSpringConfig("KVMRelated.xml");
        deployer.addSpringConfig("localStorageSimulator.xml");
        deployer.addSpringConfig("localStorage.xml");
        deployer.addSpringConfig("flatNetworkServiceSimulator.xml");
        deployer.addSpringConfig("flatNetworkProvider.xml");
        deployer.load();

        loader = deployer.getComponentLoader();
        bus = loader.getComponent(CloudBus.class);
        dbf = loader.getComponent(DatabaseFacade.class);
        config = loader.getComponent(LocalStorageSimulatorConfig.class);
        fconfig = loader.getComponent(FlatNetworkServiceSimulatorConfig.class);
        dhcpUpgradeExtension = loader.getComponent(FlatDhcpUpgradeExtension.class);

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
    public void test() throws ApiSenderException, InterruptedException {
        FlatNetworkGlobalProperty.DELETE_DEPRECATED_DHCP_NAME_SPACE = true;
        dhcpUpgradeExtension.start();

        HostInventory host = deployer.hosts.get("host1");
        api.reconnectHost(host.getUuid());

        TimeUnit.SECONDS.sleep(2);

        Assert.assertEquals(1, fconfig.deleteNamespaceCmds.size());

//
        L3NetworkInventory l3nw = deployer.l3Networks.get("TestL3Network1");

        APIGetL3NetworkDhcpIpAddressMsg msg = new APIGetL3NetworkDhcpIpAddressMsg();
        msg.setSession(session);
        msg.setL3NetworkUuid(l3nw.getUuid());
        msg.setServiceId(ApiMediatorConstant.SERVICE_ID);
        ApiSender sender = new ApiSender();
        sender.setTimeout(15000);
        APIGetL3NetworkDhcpIpAddressReply re = sender.call(msg, APIGetL3NetworkDhcpIpAddressReply.class);

        logger.info("!!!!!!APIGetL3NetworkDhcpIpAddressMsg!!!!" + re.getIp());
    }
}
