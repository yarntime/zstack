package org.zstack.sdk;

public class QueryVolumeSnapshotTreeResult {
    public java.util.List<VolumeSnapshotTreeInventory> inventories;
    public void setInventories(java.util.List<VolumeSnapshotTreeInventory> inventories) {
        this.inventories = inventories;
    }
    public java.util.List<VolumeSnapshotTreeInventory> getInventories() {
        return this.inventories;
    }

    public java.lang.Long total;
    public void setTotal(java.lang.Long total) {
        this.total = total;
    }
    public java.lang.Long getTotal() {
        return this.total;
    }

}
