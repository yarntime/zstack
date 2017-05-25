package org.zstack.test.storage.backup.sftp;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.componentloader.ComponentLoader;
import org.zstack.core.config.GlobalConfigFacade;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.header.identity.SessionInventory;
import org.zstack.header.image.ImageInventory;
import org.zstack.header.image.ImageVO;
import org.zstack.simulator.storage.backup.sftp.SftpBackupStorageSimulatorConfig;
import org.zstack.storage.backup.sftp.SftpBackupStorageInventory;
import org.zstack.test.Api;
import org.zstack.test.ApiSenderException;
import org.zstack.test.DBUtil;
import org.zstack.test.WebBeanConstructor;
import org.zstack.test.deployer.Deployer;
import org.zstack.utils.Utils;
import org.zstack.utils.data.SizeUnit;
import org.zstack.utils.logging.CLogger;

public class TestSftpBackupStorageGetImageActualSize {
    CLogger logger = Utils.getLogger(TestSftpBackupStorageGetImageActualSize.class);
    Deployer deployer;
    Api api;
    ComponentLoader loader;
    CloudBus bus;
    DatabaseFacade dbf;
    SessionInventory session;
    SftpBackupStorageSimulatorConfig config;
    GlobalConfigFacade gcf;

    @Before
    public void setUp() throws Exception {
        DBUtil.reDeployDB();
        WebBeanConstructor con = new WebBeanConstructor();
        deployer = new Deployer("deployerXml/sftpBackupStorage/TestAddSftpBackupStorage.xml", con);
        deployer.addSpringConfig("KVMRelated.xml");
        deployer.build();
        api = deployer.getApi();
        loader = deployer.getComponentLoader();
        bus = loader.getComponent(CloudBus.class);
        dbf = loader.getComponent(DatabaseFacade.class);
        gcf = loader.getComponent(GlobalConfigFacade.class);
        config = loader.getComponent(SftpBackupStorageSimulatorConfig.class);
        session = api.loginAsAdmin();
    }

    @Test
    public void test() throws ApiSenderException {
        SftpBackupStorageTestHelper helper = new SftpBackupStorageTestHelper();
        SftpBackupStorageInventory sinv = helper.addSimpleHttpBackupStorage(api);
        ImageInventory iinv = helper.addImage(api, sinv);

        long asize = SizeUnit.GIGABYTE.toByte(10);
        config.getImageSizeCmdActualSize.put(iinv.getUuid(), asize);
        long size = SizeUnit.GIGABYTE.toByte(100);
        config.getImageSizeCmdSize.put(iinv.getUuid(), size);
        iinv = api.syncImageSize(iinv.getUuid(), null);
        Assert.assertEquals(asize, iinv.getActualSize().longValue());
        Assert.assertEquals(size, iinv.getSize());

        ImageVO imvo = dbf.findByUuid(iinv.getUuid(), ImageVO.class);
        Assert.assertEquals(asize, imvo.getActualSize());
        Assert.assertEquals(size, imvo.getSize());
    }
}
