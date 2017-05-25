package org.zstack.test.core.cloudbus;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.zstack.core.cloudbus.EventFacade;
import org.zstack.core.cloudbus.EventFacadeImpl;
import org.zstack.core.cloudbus.EventRunnable;
import org.zstack.core.componentloader.ComponentLoader;
import org.zstack.test.BeanConstructor;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: frank
 * Time: 12:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestCanonicalEvent5 {
    CLogger logger = Utils.getLogger(TestCanonicalEvent5.class);
    ComponentLoader loader;
    EventFacade evtf;
    boolean success;

    @Before
    public void setUp() throws Exception {
        BeanConstructor con = new BeanConstructor();
        loader = con.build();
        evtf = loader.getComponent(EventFacade.class);
        ((EventFacadeImpl) evtf).start();
    }

    @Test
    public void test() throws InterruptedException {
        evtf.on("/?e?t/event", new EventRunnable() {
            @Override
            public void run() {
                success = true;
            }
        });

        String path = "/test/event";
        evtf.fire(path, null);
        TimeUnit.SECONDS.sleep(1);
        Assert.assertTrue(success);
    }
}

