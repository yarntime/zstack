package org.zstack.test.aop;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.componentloader.ComponentLoader;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.header.core.Completion;
import org.zstack.header.core.FutureCompletion;
import org.zstack.header.errorcode.SysErrors;
import org.zstack.test.BeanConstructor;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

/**
 */
public class TestFutureCompletion1 {
    CLogger logger = Utils.getLogger(TestFutureCompletion1.class);
    ComponentLoader loader;
    CloudBus bus;
    ErrorFacade errf;

    @Before
    public void setUp() throws Exception {
        BeanConstructor con = new BeanConstructor();
        loader = con.build();
        errf = loader.getComponent(ErrorFacade.class);
    }

    private void complete(Completion completion) {
        completion.fail(errf.stringToOperationError("on purpose"));
    }

    @Test
    public void test() throws InterruptedException {
        FutureCompletion completion = new FutureCompletion(null);
        complete(completion);
        completion.await();
        Assert.assertFalse(completion.isSuccess());
        Assert.assertEquals(SysErrors.OPERATION_ERROR.toString(), completion.getErrorCode().getCode());
    }
}
