package org.zstack.test.deployer;

import org.zstack.test.ApiSenderException;
import org.zstack.test.deployer.schema.DeployerConfig;

import java.util.List;

public interface InstanceOfferingDeployer<T> extends AbstractDeployer<T> {
    void deploy(List<T> offerings, DeployerConfig config, Deployer deployer) throws ApiSenderException;
}
