package org.zstack.test.image;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.componentloader.ComponentLoader;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.header.image.ImageConstant.ImageMediaType;
import org.zstack.header.image.ImageInventory;
import org.zstack.header.image.ImageVO;
import org.zstack.header.simulator.SimulatorConstant;
import org.zstack.header.storage.backup.BackupStorageInventory;
import org.zstack.header.storage.backup.BackupStorageStateEvent;
import org.zstack.test.Api;
import org.zstack.test.ApiSenderException;
import org.zstack.test.DBUtil;
import org.zstack.test.deployer.Deployer;

/**
 * 1. have 2 back storage
 * 2. disable all backup storage
 * 3. add image to both back storage
 * <p>
 * confirm image failed to add
 */
public class TestAddImage3 {
    Deployer deployer;
    Api api;
    ComponentLoader loader;
    CloudBus bus;
    DatabaseFacade dbf;

    @Before
    public void setUp() throws Exception {
        DBUtil.reDeployDB();
        deployer = new Deployer("deployerXml/image/TestAddImage1.xml");
        deployer.build();
        api = deployer.getApi();
        loader = deployer.getComponentLoader();
        bus = loader.getComponent(CloudBus.class);
        dbf = loader.getComponent(DatabaseFacade.class);
    }

    @Test
    public void test() throws InterruptedException, ApiSenderException {
        BackupStorageInventory bs1 = deployer.backupStorages.get("TestBackupStorage");
        BackupStorageInventory bs2 = deployer.backupStorages.get("TestBackupStorage1");

        api.changeBackupStorageState(bs2.getUuid(), BackupStorageStateEvent.disable);
        api.changeBackupStorageState(bs1.getUuid(), BackupStorageStateEvent.disable);

        ImageInventory iinv = new ImageInventory();
        iinv.setName("Test Image");
        iinv.setDescription("Test Image");
        iinv.setMediaType(ImageMediaType.RootVolumeTemplate.toString());
        iinv.setGuestOsType("Window7");
        iinv.setFormat(SimulatorConstant.SIMULATOR_VOLUME_FORMAT_STRING);
        iinv.setUrl("http://zstack.org/download/win7.qcow2");
        try {
            api.addImage(iinv, bs1.getUuid(), bs2.getUuid());
        } catch (ApiSenderException e) {
            long count = dbf.count(ImageVO.class);
            Assert.assertEquals(0, count);
        }
    }
}
