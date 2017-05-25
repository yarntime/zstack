package org.zstack.test.image;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.componentloader.ComponentLoader;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.header.identity.SessionInventory;
import org.zstack.header.image.ImageInventory;
import org.zstack.header.image.ImageStatus;
import org.zstack.header.image.ImageVO;
import org.zstack.header.storage.backup.BackupStorageInventory;
import org.zstack.header.storage.backup.BackupStorageStateEvent;
import org.zstack.header.vm.VmInstanceInventory;
import org.zstack.header.volume.VolumeVO;
import org.zstack.simulator.storage.backup.sftp.SftpBackupStorageSimulatorConfig;
import org.zstack.test.Api;
import org.zstack.test.ApiSenderException;
import org.zstack.test.DBUtil;
import org.zstack.test.WebBeanConstructor;
import org.zstack.test.deployer.Deployer;
import org.zstack.test.storage.backup.sftp.TestSftpBackupStorageDeleteImage2;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

import java.util.Arrays;

/**
 * 1. create image from root volume on two backup storage
 * 2. disable sftp
 * <p>
 * confirm creation only succeeds on sftp1
 */
public class TestCreateTemplateFromRootVolumeFailure5 {
    CLogger logger = Utils.getLogger(TestSftpBackupStorageDeleteImage2.class);
    Deployer deployer;
    Api api;
    ComponentLoader loader;
    CloudBus bus;
    DatabaseFacade dbf;
    SessionInventory session;
    SftpBackupStorageSimulatorConfig config;

    @Before
    public void setUp() throws Exception {
        DBUtil.reDeployDB();
        WebBeanConstructor con = new WebBeanConstructor();
        deployer = new Deployer("deployerXml/image/TestCreateTemplateFromRootVolume1.xml", con);
        deployer.addSpringConfig("KVMRelated.xml");
        deployer.build();
        api = deployer.getApi();
        loader = deployer.getComponentLoader();
        bus = loader.getComponent(CloudBus.class);
        dbf = loader.getComponent(DatabaseFacade.class);
        config = loader.getComponent(SftpBackupStorageSimulatorConfig.class);
        session = api.loginAsAdmin();
    }

    @Test
    public void test() throws ApiSenderException {
        VmInstanceInventory vm = deployer.vms.get("TestVm");
        api.stopVmInstance(vm.getUuid());
        String rootVolumeUuid = vm.getRootVolumeUuid();
        VolumeVO vol = dbf.findByUuid(rootVolumeUuid, VolumeVO.class);

        BackupStorageInventory sftp = deployer.backupStorages.get("sftp");
        BackupStorageInventory sftp1 = deployer.backupStorages.get("sftp1");
        api.changeBackupStorageState(sftp.getUuid(), BackupStorageStateEvent.disable);
        ImageInventory image = api.createTemplateFromRootVolume("testImage", rootVolumeUuid, Arrays.asList(sftp.getUuid(), sftp1.getUuid()));
        Assert.assertEquals(1, image.getBackupStorageRefs().size());
        Assert.assertEquals(ImageStatus.Ready.toString(), image.getStatus());
        Assert.assertEquals(vol.getSize(), image.getSize());
        Assert.assertEquals(String.format("volume://%s", vol.getUuid()), image.getUrl());
        Assert.assertEquals(sftp1.getUuid(), image.getBackupStorageRefs().get(0).getBackupStorageUuid());

        ImageVO ivo = dbf.findByUuid(image.getUuid(), ImageVO.class);
        Assert.assertNotNull(ivo);
    }

}
