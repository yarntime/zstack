package org.zstack.test.deployer;

import org.zstack.test.ApiSenderException;
import org.zstack.test.deployer.schema.DeployerConfig;

import java.util.List;

public interface VmDeployer<T> extends AbstractDeployer<T> {
    void deploy(List<T> vms, DeployerConfig config, Deployer deployer) throws ApiSenderException;
}
