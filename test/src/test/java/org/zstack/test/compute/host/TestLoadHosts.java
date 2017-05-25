package org.zstack.test.compute.host;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.zstack.core.componentloader.ComponentLoader;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.SimpleQuery;
import org.zstack.core.db.SimpleQuery.Op;
import org.zstack.header.host.HostStatus;
import org.zstack.header.host.HostVO;
import org.zstack.header.host.HostVO_;
import org.zstack.test.*;

import java.util.concurrent.TimeUnit;

public class TestLoadHosts {
    Api api;
    ComponentLoader loader;
    DatabaseFacade dbf;

    @Before
    public void setUp() throws Exception {
        UnitTestUtils.runTestCase(CreateHost.class, "-Dhost.num=100");
        BeanConstructor con = new WebBeanConstructor();
        /* This loads spring application context */
        loader = con.addXml("PortalForUnitTest.xml")
                .addXml("ClusterManager.xml")
                .addXml("ZoneManager.xml")
                .addXml("HostManager.xml")
                .addXml("Simulator.xml")
                .addXml("HostAllocatorManager.xml")
                .addXml("AccountManager.xml").build();
        dbf = loader.getComponent(DatabaseFacade.class);
        api = new Api();
        api.startServer();
    }

    @Test
    public void test() throws ApiSenderException, InterruptedException {
        try {
            /*wait 60 secs for 100 host connected, we are async, have to sleep to prevent test case over */
            TimeUnit.SECONDS.sleep(60);
            SimpleQuery<HostVO> query = dbf.createQuery(HostVO.class);
            query.add(HostVO_.status, Op.EQ, HostStatus.Connected);
            long count = query.count();
            Assert.assertEquals(100, count);
        } finally {
            api.stopServer();
        }
    }
}
