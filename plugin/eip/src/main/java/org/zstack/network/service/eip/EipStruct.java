package org.zstack.network.service.eip;

import org.zstack.header.vm.VmNicInventory;
import org.zstack.network.service.vip.VipInventory;

/**
 */
public class EipStruct {
    private EipInventory eip;
    private VmNicInventory nic;
    private VipInventory vip;
    private boolean snatInboundTraffic;

    public boolean isSnatInboundTraffic() {
        return snatInboundTraffic;
    }

    public void setSnatInboundTraffic(boolean snatInboundTraffic) {
        this.snatInboundTraffic = snatInboundTraffic;
    }

    public EipInventory getEip() {
        return eip;
    }

    public void setEip(EipInventory eip) {
        this.eip = eip;
    }

    public VmNicInventory getNic() {
        return nic;
    }

    public void setNic(VmNicInventory nic) {
        this.nic = nic;
    }

    public VipInventory getVip() {
        return vip;
    }

    public void setVip(VipInventory vip) {
        this.vip = vip;
    }
}
