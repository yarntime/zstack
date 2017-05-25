package org.zstack.network.securitygroup;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

import java.sql.Timestamp;

/**
 * Created by frank on 6/15/2015.
 */
@RestResponse(allTo = "inventory")
public class APIUpdateSecurityGroupEvent extends APIEvent {
    private SecurityGroupInventory inventory;

    public APIUpdateSecurityGroupEvent() {
    }

    public APIUpdateSecurityGroupEvent(String apiId) {
        super(apiId);
    }

    public SecurityGroupInventory getInventory() {
        return inventory;
    }

    public void setInventory(SecurityGroupInventory inventory) {
        this.inventory = inventory;
    }
 
    public static APIUpdateSecurityGroupEvent __example__() {
        APIUpdateSecurityGroupEvent event = new APIUpdateSecurityGroupEvent();
        SecurityGroupInventory sec = new SecurityGroupInventory();
        sec.setUuid(uuid());
        sec.setName("web");
        sec.setDescription("for test");
        sec.setState("Enabled");
        sec.setCreateDate(new Timestamp(System.currentTimeMillis()));
        sec.setLastOpDate(new Timestamp(System.currentTimeMillis()));
        event.setInventory(sec);
        event.setSuccess(true);
        return event;
    }

}
