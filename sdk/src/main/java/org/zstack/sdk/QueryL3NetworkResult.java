package org.zstack.sdk;

public class QueryL3NetworkResult {
    public java.util.List<L3NetworkInventory> inventories;
    public void setInventories(java.util.List<L3NetworkInventory> inventories) {
        this.inventories = inventories;
    }
    public java.util.List<L3NetworkInventory> getInventories() {
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
