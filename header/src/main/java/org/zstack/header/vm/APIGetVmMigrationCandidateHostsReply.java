package org.zstack.header.vm;

import org.zstack.header.host.HostInventory;
import org.zstack.header.message.APIReply;
import org.zstack.header.rest.RestResponse;

import java.sql.Timestamp;
import java.util.List;

import static java.util.Arrays.asList;

/**
 */
@RestResponse(allTo = "inventories")
public class APIGetVmMigrationCandidateHostsReply extends APIReply {
    private List<HostInventory> inventories;

    public List<HostInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<HostInventory> inventories) {
        this.inventories = inventories;
    }
 
    public static APIGetVmMigrationCandidateHostsReply __example__() {
        APIGetVmMigrationCandidateHostsReply reply = new APIGetVmMigrationCandidateHostsReply();

        HostInventory hi = new HostInventory ();
        hi.setAvailableCpuCapacity(2L);
        hi.setAvailableMemoryCapacity(4L);
        hi.setClusterUuid(uuid());
        hi.setManagementIp("192.168.0.1");
        hi.setName("example");
        hi.setState("Enabled");
        hi.setStatus("Connected");
        hi.setClusterUuid(uuid());
        hi.setZoneUuid(uuid());
        hi.setUuid(uuid());
        hi.setTotalCpuCapacity(4L);
        hi.setTotalMemoryCapacity(4L);
        hi.setHypervisorType("KVM");
        hi.setDescription("example");
        hi.setCreateDate(new Timestamp(System.currentTimeMillis()));
        hi.setLastOpDate(new Timestamp(System.currentTimeMillis()));

        reply.setInventories(asList(hi));

        return reply;
    }

}
