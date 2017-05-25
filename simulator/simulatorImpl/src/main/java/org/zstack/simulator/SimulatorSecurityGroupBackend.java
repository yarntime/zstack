package org.zstack.simulator;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.SimpleQuery;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.header.core.Completion;
import org.zstack.header.errorcode.ErrorCode;
import org.zstack.header.host.HypervisorType;
import org.zstack.header.simulator.SimulatorConstant;
import org.zstack.header.vm.*;
import org.zstack.network.securitygroup.*;
import org.zstack.utils.CollectionUtils;
import org.zstack.utils.Utils;
import org.zstack.utils.function.Function;
import org.zstack.utils.function.ListFunction;
import org.zstack.utils.gson.JSONObjectUtil;
import org.zstack.utils.logging.CLogger;

import java.util.*;


public class SimulatorSecurityGroupBackend implements
		SecurityGroupHypervisorBackend, VmInstanceStateChangeNotifyPoint {
	private static CLogger logger = Utils.getLogger(SimulatorSecurityGroupBackend.class);
	
	private Map<String, Set<SecurityGroupRuleTO>> rules = new HashMap<String, Set<SecurityGroupRuleTO>>();
	
	public volatile boolean securityGroupSuccess = true;
	
	@Autowired
	private CloudBus bus;
    @Autowired
    private ErrorFacade errf;
    @Autowired
    private DatabaseFacade dbf;

	@Override
	public void applyRules(HostRuleTO hto, Completion complete) {
		logger.debug(String.format("apply security rules to simulator host[uuid:%s]:\n%s", hto.getHostUuid(), JSONObjectUtil.toJsonString(hto.getRules())));
		if (!securityGroupSuccess) {
		    ErrorCode errorCode = errf.stringToOperationError("on purpose");
		    complete.fail(errorCode);
		    return;
		}
		
		if (hto.isRefreshHost()) {
		    rules.remove(hto.getHostUuid());
		}
		
		Set<SecurityGroupRuleTO> oldTos = rules.get(hto.getHostUuid());
		if (oldTos == null) {
		    Set<SecurityGroupRuleTO> tos = new HashSet<SecurityGroupRuleTO>();
		    tos.addAll(hto.getRules());
		    rules.put(hto.getHostUuid(), tos);
		} else {
		    oldTos.removeAll(hto.getRules());
		    oldTos.addAll(hto.getRules());
		}
		complete.success();
	}

    @Override
    public void cleanUpUnusedRuleOnHost(String hostUuid, Completion completion) {
        SimpleQuery<VmInstanceVO> q = dbf.createQuery(VmInstanceVO.class);
        q.add(VmInstanceVO_.hostUuid, SimpleQuery.Op.EQ, hostUuid);
        List<VmInstanceVO> vms = q.list();
        List<VmNicVO> nics = CollectionUtils.transformToList(vms, new ListFunction<VmNicVO, VmInstanceVO>() {
            @Override
            public List<VmNicVO> call(VmInstanceVO arg) {
                List<VmNicVO> lst = new ArrayList<VmNicVO>();
                lst.addAll(arg.getVmNics());
                return lst;
            }
        });
        List<String> nicNames = CollectionUtils.transformToList(nics, new Function<String, VmNicVO>() {
            @Override
            public String call(VmNicVO arg) {
                return arg.getInternalName();
            }
        });
        Set<SecurityGroupRuleTO> tos = rules.get(hostUuid);
        Set<SecurityGroupRuleTO> ntos = new HashSet<SecurityGroupRuleTO>();
        for (SecurityGroupRuleTO to : tos) {
            if (nicNames.contains(to.getVmNicInternalName())) {
                ntos.add(to);
            }
        }
        rules.put(hostUuid, ntos);
        completion.success();
    }

    public Set<SecurityGroupRuleTO> getRulesOnHost(String hostUuid) {
		return rules.get(hostUuid);
	}
	
	public SecurityGroupRuleTO getRulesOnHost(String hostUuid, String vmNicName) {
        Set<SecurityGroupRuleTO> tos = getRulesOnHost(hostUuid);
        if (tos == null) {
            return null;
        }

	    for (SecurityGroupRuleTO to : tos) {
	        if (to.getVmNicInternalName().equals(vmNicName)) {
	            return to;
	        }
	    }
	    
	    return null;
	}
	
	public List<RuleTO> getRulesByNicName(String nicName) {
		for (Set<SecurityGroupRuleTO> rs : rules.values()) {
			for (SecurityGroupRuleTO sto : rs) {
				if (sto.getVmNicInternalName().equals(nicName)) {
					return sto.getRules();
				}
			}
		}
		
		return null;
	}

	@Override
	public HypervisorType getSecurityGroupBackendHypervisorType() {
		return HypervisorType.valueOf(SimulatorConstant.SIMULATOR_HYPERVISOR_TYPE);
	}

    @Override
    public void notifyVmInstanceStateChange(VmInstanceInventory inv, VmInstanceState previousState, VmInstanceState currentState) {
        if (previousState == VmInstanceState.Unknown && currentState == VmInstanceState.Running) {
            RefreshSecurityGroupRulesOnVmMsg msg = new RefreshSecurityGroupRulesOnVmMsg();
            msg.setVmInstanceUuid(inv.getUuid());
            msg.setServiceId(bus.makeLocalServiceId(SecurityGroupConstant.SERVICE_ID));
            bus.send(msg);
        }
    }

    @Override
    public HypervisorType getSupportedHypervisorTypeForVmInstanceStateChangeNotifyPoint() {
        return HypervisorType.valueOf(SimulatorConstant.SIMULATOR_HYPERVISOR_TYPE);
    }

}
