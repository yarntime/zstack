package org.zstack.sdk;

public class AddMonToCephPrimaryStorageResult {
    public CephPrimaryStorageInventory inventory;
    public void setInventory(CephPrimaryStorageInventory inventory) {
        this.inventory = inventory;
    }
    public CephPrimaryStorageInventory getInventory() {
        return this.inventory;
    }

}
