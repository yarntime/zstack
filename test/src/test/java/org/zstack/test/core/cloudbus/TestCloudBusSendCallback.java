package org.zstack.test.core.cloudbus;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.zstack.core.cloudbus.CloudBusCallBack;
import org.zstack.core.cloudbus.CloudBusIN;
import org.zstack.core.componentloader.ComponentLoader;
import org.zstack.header.AbstractService;
import org.zstack.header.Service;
import org.zstack.header.message.Message;
import org.zstack.header.message.MessageReply;
import org.zstack.header.message.NeedReplyMessage;
import org.zstack.test.BeanConstructor;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestCloudBusSendCallback {
    CLogger logger = Utils.getLogger(TestCloudBusSendCallback.class);
    ComponentLoader loader;
    CloudBusIN bus;
    CountDownLatch latch = new CountDownLatch(1);
    boolean isSuccess = false;
    Service serv;

    public static class HelloWorldMsg extends NeedReplyMessage {
        private String greet;

        public String getGreet() {
            return greet;
        }

        public void setGreet(String greet) {
            this.greet = greet;
        }

    }

    public static class HelloWorldReply extends MessageReply {
        private String greet;

        public String getGreet() {
            return greet;
        }

        public void setGreet(String greet) {
            this.greet = greet;
        }
    }

    class FakeService extends AbstractService {
        @Override
        public boolean start() {
            bus.registerService(this);
            bus.activeService(this);
            return true;
        }

        @Override
        public boolean stop() {
            bus.deActiveService(this);
            bus.unregisterService(this);
            return true;
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.getClass() == HelloWorldMsg.class) {
                HelloWorldMsg hmsg = (HelloWorldMsg) msg;
                HelloWorldReply r = new HelloWorldReply();
                r.setGreet(hmsg.getGreet());
                bus.reply(msg, r);
            }
        }

        @Override
        public String getId() {
            return this.getClass().getCanonicalName();
        }

    }

    @Before
    public void setUp() throws Exception {
        BeanConstructor con = new BeanConstructor();
        loader = con.build();
        bus = loader.getComponent(CloudBusIN.class);
        serv = new FakeService();
        serv.start();
    }

    @Test
    public void test() throws InterruptedException, ClassNotFoundException {
        HelloWorldMsg msg = new HelloWorldMsg();
        msg.setGreet("Hello");
        msg.setServiceId(FakeService.class.getCanonicalName());
        msg.setTimeout(TimeUnit.SECONDS.toMillis(10));
        bus.send(msg, new CloudBusCallBack(null) {
            @Override
            public void run(MessageReply reply) {
                if (reply instanceof HelloWorldReply) {
                    HelloWorldReply hr = (HelloWorldReply) reply;
                    if ("Hello".equals(hr.getGreet())) {
                        isSuccess = true;
                    }
                }
                latch.countDown();
            }
        });
        latch.await(15, TimeUnit.SECONDS);
        serv.stop();
        Assert.assertEquals(true, isSuccess);
    }
}
