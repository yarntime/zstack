package org.zstack.sdk;

public class QueryUserTagResult {
    public java.util.List<UserTagInventory> inventories;
    public void setInventories(java.util.List<UserTagInventory> inventories) {
        this.inventories = inventories;
    }
    public java.util.List<UserTagInventory> getInventories() {
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
