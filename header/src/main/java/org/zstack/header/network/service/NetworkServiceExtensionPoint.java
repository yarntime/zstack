package org.zstack.header.network.service;

import org.zstack.header.core.Completion;
import org.zstack.header.core.NoErrorCompletion;
import org.zstack.header.vm.VmInstanceSpec;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: frank
 * Time: 7:48 PM
 * To change this template use File | Settings | File Templates.
 */
public interface NetworkServiceExtensionPoint {
    public static enum NetworkServiceExtensionPosition {
        BEFORE_VM_CREATED,
        AFTER_VM_CREATED
    }

    NetworkServiceExtensionPosition getNetworkServiceExtensionPosition();

    NetworkServiceType getNetworkServiceType();

    void applyNetworkService(VmInstanceSpec servedVm, Map<String, Object> data, Completion completion);

    void releaseNetworkService(VmInstanceSpec servedVm, Map<String, Object> data, NoErrorCompletion completion);
}
