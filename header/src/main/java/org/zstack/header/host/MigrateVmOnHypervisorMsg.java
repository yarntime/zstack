package org.zstack.header.host;

import org.zstack.header.message.NeedReplyMessage;
import org.zstack.header.vm.VmInstanceInventory;

public class MigrateVmOnHypervisorMsg extends NeedReplyMessage implements HostMessage {
    public static enum StorageMigrationPolicy {
        FullCopy,
        IncCopy
    }

    private VmInstanceInventory vmInventory;
    private HostInventory destHostInventory;
    private String srcHostUuid;
    private StorageMigrationPolicy storageMigrationPolicy;

    public StorageMigrationPolicy getStorageMigrationPolicy() {
        return storageMigrationPolicy;
    }

    public void setStorageMigrationPolicy(StorageMigrationPolicy storageMigrationPolicy) {
        this.storageMigrationPolicy = storageMigrationPolicy;
    }

    public VmInstanceInventory getVmInventory() {
        return vmInventory;
    }

    public void setVmInventory(VmInstanceInventory vmInventory) {
        this.vmInventory = vmInventory;
    }

    public HostInventory getDestHostInventory() {
        return destHostInventory;
    }

    public void setDestHostInventory(HostInventory destHostInventory) {
        this.destHostInventory = destHostInventory;
    }

    @Override
    public String getHostUuid() {
        return getSrcHostUuid();
    }

    public String getSrcHostUuid() {
        return srcHostUuid;
    }

    public void setSrcHostUuid(String srcHostUuid) {
        this.srcHostUuid = srcHostUuid;
    }
}
