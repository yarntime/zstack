package org.zstack.header.tag;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 */
@Entity
@Table
public class SystemTagVO extends TagAO {
    @Column
    private boolean inherent;

    public SystemTagVO(SystemTagVO other) {
        super(other);
        this.inherent = other.inherent;
    }

    public boolean isInherent() {
        return inherent;
    }

    public void setInherent(boolean inherent) {
        this.inherent = inherent;
    }

    public SystemTagVO() {
        setType(TagType.System);
    }
}
