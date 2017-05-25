package org.zstack.header.storage.primary;

import org.zstack.header.vo.ForeignKey;
import org.zstack.header.vo.ForeignKey.ReferenceOption;
import org.zstack.header.vo.Index;
import org.zstack.header.vo.ShadowEntity;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 */
@Entity
@Table
public class PrimaryStorageCapacityVO implements ShadowEntity {
    @Column
    @Id
    @ForeignKey(parentEntityClass = PrimaryStorageEO.class, onDeleteAction = ReferenceOption.CASCADE)
    private String uuid;

    @Column
    @Index
    private long totalCapacity;

    @Column
    @Index
    private long availableCapacity;

    @Column
    @Index
    private long totalPhysicalCapacity;

    @Column
    @Index
    private long availablePhysicalCapacity;

    @Column
    private Long systemUsedCapacity;

    @Column
    private Timestamp createDate;

    @Column
    private Timestamp lastOpDate;

    @Transient
    private PrimaryStorageCapacityVO shadow;

    public PrimaryStorageCapacityVO getShadow() {
        return shadow;
    }

    @PreUpdate
    private void preUpdate() {
        lastOpDate = null;
    }

    public Long getSystemUsedCapacity() {
        return systemUsedCapacity;
    }

    public void setSystemUsedCapacity(Long systemUsedCapacity) {
        this.systemUsedCapacity = systemUsedCapacity;
    }

    public long getTotalPhysicalCapacity() {
        return totalPhysicalCapacity;
    }

    public void setTotalPhysicalCapacity(long totalPhysicalCapacity) {
        this.totalPhysicalCapacity = totalPhysicalCapacity;
    }

    public long getAvailablePhysicalCapacity() {
        return availablePhysicalCapacity;
    }

    public void setAvailablePhysicalCapacity(long availablePhysicalCapacity) {
        this.availablePhysicalCapacity = availablePhysicalCapacity;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(long totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public long getAvailableCapacity() {
        return availableCapacity;
    }

    public void setAvailableCapacity(long availableCapacity) {
        this.availableCapacity = availableCapacity;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }

    @Override
    public void setShadow(Object o) {
        shadow = (PrimaryStorageCapacityVO) o;
    }
}
