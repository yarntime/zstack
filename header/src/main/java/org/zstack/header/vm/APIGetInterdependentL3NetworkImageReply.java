package org.zstack.header.vm;

import org.zstack.header.message.APIReply;
import org.zstack.header.network.l3.IpRangeInventory;
import org.zstack.header.network.l3.L3NetworkConstant;
import org.zstack.header.network.l3.L3NetworkInventory;
import org.zstack.header.network.l3.L3NetworkState;
import org.zstack.header.network.service.NetworkServiceL3NetworkRefInventory;
import org.zstack.header.network.service.NetworkServiceType;
import org.zstack.header.rest.RestResponse;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.codehaus.groovy.runtime.InvokerHelper.asList;

/**
 * Created by xing5 on 2016/8/23.
 */
@RestResponse(allTo = "inventories")
public class APIGetInterdependentL3NetworkImageReply extends APIReply {
    private List inventories;

    public List getInventories() {
        return inventories;
    }

    public void setInventories(List inventories) {
        this.inventories = inventories;
    }
 
    public static APIGetInterdependentL3NetworkImageReply __example__() {
        APIGetInterdependentL3NetworkImageReply reply = new APIGetInterdependentL3NetworkImageReply();

        L3NetworkInventory l3 = new L3NetworkInventory();
        l3.setName("private L3");

        Timestamp time = new Timestamp(System.currentTimeMillis());

        String l3Uuid = uuid();
        l3.setUuid(l3Uuid);
        l3.setType(L3NetworkConstant.L3_BASIC_NETWORK_TYPE);
        l3.setState(L3NetworkState.Enabled.toString());
        l3.setL2NetworkUuid(uuid());
        l3.setZoneUuid(uuid());
        l3.setCreateDate(time);
        l3.setLastOpDate(time);

        IpRangeInventory ipr = new IpRangeInventory();
        ipr.setName("ip range");
        ipr.setUuid(uuid());
        ipr.setL3NetworkUuid(l3Uuid);
        ipr.setStartIp("192.168.0.10");
        ipr.setEndIp("192.168.0.100");
        ipr.setNetmask("255.255.255.0");
        ipr.setGateway("192.168.0.1");
        ipr.setCreateDate(time);
        ipr.setLastOpDate(time);
        l3.setIpRanges(asList(ipr));

        List<NetworkServiceL3NetworkRefInventory> refs = new ArrayList<>();
        String puuid = uuid();

        NetworkServiceL3NetworkRefInventory ref = new NetworkServiceL3NetworkRefInventory();
        ref.setL3NetworkUuid(l3Uuid);
        ref.setNetworkServiceProviderUuid(puuid);
        ref.setNetworkServiceType(NetworkServiceType.DHCP.toString());
        refs.add(ref);

        ref = new NetworkServiceL3NetworkRefInventory();
        ref.setL3NetworkUuid(l3Uuid);
        ref.setNetworkServiceProviderUuid(puuid);
        ref.setNetworkServiceType(NetworkServiceType.DNS.toString());
        refs.add(ref);

        ref = new NetworkServiceL3NetworkRefInventory();
        ref.setL3NetworkUuid(l3Uuid);
        ref.setNetworkServiceProviderUuid(puuid);
        ref.setNetworkServiceType(NetworkServiceType.SNAT.toString());
        refs.add(ref);

        l3.setNetworkServices(refs);

        reply.inventories = asList(l3);

        return reply;
    }

}
