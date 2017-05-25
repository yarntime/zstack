package org.zstack.header.storage.snapshot;

/**
 */
public class VolumeSnapshotStruct {
    private VolumeSnapshotInventory parent;
    private VolumeSnapshotInventory current;
    private boolean fullSnapshot;

    public boolean isFullSnapshot() {
        return fullSnapshot;
    }

    public void setFullSnapshot(boolean fullSnapshot) {
        this.fullSnapshot = fullSnapshot;
    }

    public VolumeSnapshotInventory getParent() {
        return parent;
    }

    public void setParent(VolumeSnapshotInventory parent) {
        this.parent = parent;
    }

    public VolumeSnapshotInventory getCurrent() {
        return current;
    }

    public void setCurrent(VolumeSnapshotInventory current) {
        this.current = current;
    }
}
