package org.zstack.network.service.virtualrouter.vyos;

import org.zstack.header.core.ReturnValueCompletion;
import org.zstack.header.network.service.NetworkServiceProviderType;
import org.zstack.network.service.virtualrouter.VirtualRouterStruct;
import org.zstack.network.service.virtualrouter.VirtualRouterVmInventory;
import org.zstack.network.service.virtualrouter.portforwarding.VirtualRouterPortForwardingBackend;

/**
 * Created by xing5 on 2016/10/31.
 */
public class VyosPortForwardingBackend extends VirtualRouterPortForwardingBackend {
    @Override
    protected void acquireVirtualRouterVm(VirtualRouterStruct struct, ReturnValueCompletion<VirtualRouterVmInventory> completion) {
        struct.setApplianceVmType(VyosConstants.VYOS_VM_TYPE);
        struct.setProviderType(VyosConstants.VYOS_ROUTER_PROVIDER_TYPE);
        struct.setVirtualRouterOfferingSelector(new VyosOfferingSelector());
        struct.setApplianceVmAgentPort(7272);
        super.acquireVirtualRouterVm(struct, completion);
    }

    @Override
    public NetworkServiceProviderType getProviderType() {
        return VyosConstants.PROVIDER_TYPE;
    }
}
