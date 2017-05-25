package org.zstack.test.core.cloudbus;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.zstack.core.cloudbus.CloudBusIN;
import org.zstack.core.cloudbus.CloudBusListCallBack;
import org.zstack.core.componentloader.ComponentLoader;
import org.zstack.header.AbstractService;
import org.zstack.header.Service;
import org.zstack.header.message.Message;
import org.zstack.header.message.MessageReply;
import org.zstack.header.message.NeedReplyMessage;
import org.zstack.test.BeanConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestCloudBusSendMultiMsg1 {
    ComponentLoader loader;
    CloudBusIN bus;
    CountDownLatch latch = new CountDownLatch(1);
    boolean isSuccess = false;
    Service serv;
    int msgNum = 10;

    public static class HelloWorldMsg extends NeedReplyMessage {
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
                bus.reply(msg, new MessageReply());
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
        List<HelloWorldMsg> msgs = new ArrayList<HelloWorldMsg>(msgNum);
        for (int i = 0; i < msgNum; i++) {
            HelloWorldMsg msg = new HelloWorldMsg();
            msg.setServiceId(FakeService.class.getCanonicalName());
            msgs.add(msg);
        }

        bus.send(msgs, new CloudBusListCallBack(null) {
            @Override
            public void run(List<MessageReply> replies) {
                isSuccess = true;
                if (replies.size() == msgNum) {
                    for (MessageReply r : replies) {
                        if (!r.isSuccess()) {
                            isSuccess = false;
                        }
                    }
                } else {
                    isSuccess = false;
                }

                latch.countDown();
            }
        });

        latch.await(10, TimeUnit.SECONDS);
        serv.stop();
        Assert.assertEquals(true, isSuccess);
    }
}
