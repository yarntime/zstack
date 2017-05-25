package org.zstack.test.compute.cluster;

import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;
import org.zstack.core.componentloader.ComponentLoader;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.header.cluster.ClusterInventory;
import org.zstack.header.cluster.ClusterVO;
import org.zstack.header.zone.ZoneInventory;
import org.zstack.test.*;

public class TestCreateCluster {
    Api api;
    ComponentLoader loader;
    DatabaseFacade dbf;

    @Before
    public void setUp() throws Exception {
        DBUtil.reDeployDB();
        BeanConstructor con = new WebBeanConstructor();
        /* This loads spring application context */
        loader = con.addXml("PortalForUnitTest.xml").addXml("ZoneManager.xml").addXml("ClusterManager.xml").addXml("AccountManager.xml").build();
        dbf = loader.getComponent(DatabaseFacade.class);
        api = new Api();
        api.startServer();
    }

    @Test
    public void test() throws InterruptedException, ApiSenderException {
        try {
            ZoneInventory zone = api.createZones(1).get(0);
            ClusterInventory cluster = api.createClusters(1, zone.getUuid()).get(0);
            ClusterVO vo = dbf.findByUuid(cluster.getUuid(), ClusterVO.class);
            Assert.notNull(vo);
        } finally {
            api.stopServer();
        }
    }

}
