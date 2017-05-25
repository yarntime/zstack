package org.zstack.network.securitygroup;

import org.zstack.header.query.APIQueryReply;
import org.zstack.header.rest.RestResponse;

import java.sql.Timestamp;
import java.util.List;

import static java.util.Arrays.asList;

@RestResponse(allTo = "inventories")
public class APIQuerySecurityGroupReply extends APIQueryReply {
    private List<SecurityGroupInventory> inventories;

    public List<SecurityGroupInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<SecurityGroupInventory> inventories) {
        this.inventories = inventories;
    }
 
    public static APIQuerySecurityGroupReply __example__() {
        APIQuerySecurityGroupReply reply = new APIQuerySecurityGroupReply();
        SecurityGroupInventory sec = new SecurityGroupInventory();
        sec.setUuid(uuid());
        sec.setName("web");
        sec.setDescription("for test");
        sec.setState("Enabled");
        sec.setCreateDate(new Timestamp(System.currentTimeMillis()));
        sec.setLastOpDate(new Timestamp(System.currentTimeMillis()));
        reply.setInventories(asList(sec));
        reply.setSuccess(true);
        return reply;
    }

}
