package org.zstack.sdk;

public class QueryPrimaryStorageResult {
    public java.util.List<PrimaryStorageInventory> inventories;
    public void setInventories(java.util.List<PrimaryStorageInventory> inventories) {
        this.inventories = inventories;
    }
    public java.util.List<PrimaryStorageInventory> getInventories() {
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
