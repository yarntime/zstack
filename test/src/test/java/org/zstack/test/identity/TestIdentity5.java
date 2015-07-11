package org.zstack.test.identity;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.zstack.core.componentloader.ComponentLoader;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.SimpleQuery;
import org.zstack.core.db.SimpleQuery.Op;
import org.zstack.header.identity.AccountConstant.StatementEffect;
import org.zstack.header.identity.PolicyInventory;
import org.zstack.header.identity.PolicyInventory.Statement;
import org.zstack.header.identity.UserInventory;
import org.zstack.header.identity.UserPolicyRefVO;
import org.zstack.header.identity.UserPolicyRefVO_;
import org.zstack.test.Api;
import org.zstack.test.ApiSenderException;
import org.zstack.test.BeanConstructor;
import org.zstack.test.DBUtil;
import org.zstack.test.deployer.Deployer;

public class TestIdentity5 {
    Deployer deployer;
    Api api;
    ComponentLoader loader;
    DatabaseFacade dbf;

    @Before
    public void setUp() throws Exception {
        DBUtil.reDeployDB();
        BeanConstructor con = new BeanConstructor();
        /* This loads spring application context */
        loader = con.addXml("PortalForUnitTest.xml").addXml("AccountManager.xml").build();
        dbf = loader.getComponent(DatabaseFacade.class);
        api = new Api();
        api.startServer();
    }

    @Test
    public void test() throws ApiSenderException {
        IdentityCreator creator = new IdentityCreator(api);
        creator.createAccount("test", "test");
        UserInventory u = creator.createUser("test", "test");

        Statement s = new Statement();
        s.addAction(".*");
        s.setEffect(StatementEffect.Allow);
        PolicyInventory p = creator.createPolicy("test", s);
        creator.attachPolicyToUser("test", "test");

        SimpleQuery<UserPolicyRefVO> q = dbf.createQuery(UserPolicyRefVO.class);
        q.add(UserPolicyRefVO_.userUuid, Op.EQ, u.getUuid());
        q.add(UserPolicyRefVO_.policyUuid, Op.EQ, p.getUuid());
        Assert.assertTrue(q.isExists());
    }
}