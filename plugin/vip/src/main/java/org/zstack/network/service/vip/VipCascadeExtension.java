package org.zstack.network.service.vip;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.core.cascade.AbstractAsyncCascadeExtension;
import org.zstack.core.cascade.CascadeAction;
import org.zstack.core.cascade.CascadeConstant;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.cloudbus.CloudBusListCallBack;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.SimpleQuery;
import org.zstack.core.db.SimpleQuery.Op;
import org.zstack.header.core.Completion;
import org.zstack.header.message.MessageReply;
import org.zstack.header.network.l3.IpRangeInventory;
import org.zstack.header.network.l3.IpRangeVO;
import org.zstack.header.network.l3.L3NetworkInventory;
import org.zstack.header.network.l3.L3NetworkVO;
import org.zstack.utils.CollectionUtils;
import org.zstack.utils.Utils;
import org.zstack.utils.function.Function;
import org.zstack.utils.logging.CLogger;

import java.util.Arrays;
import java.util.List;

/**
 */
public class VipCascadeExtension extends AbstractAsyncCascadeExtension {
    private static final CLogger logger = Utils.getLogger(VipCascadeExtension.class);
    private static final String NAME = VipVO.class.getSimpleName();

    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private CloudBus bus;

    public void asyncCascade(CascadeAction action, Completion completion) {
        if (action.isActionCode(CascadeConstant.DELETION_CHECK_CODE)) {
            handleDeletionCheck(action, completion);
        } else if (action.isActionCode(CascadeConstant.DELETION_DELETE_CODE, CascadeConstant.DELETION_FORCE_DELETE_CODE)) {
            handleDeletion(action, completion);
        } else if (action.isActionCode(CascadeConstant.DELETION_CLEANUP_CODE)) {
            handleDeletionCleanup(action, completion);
        } else {
            completion.success();
        }
    }

    private void handleDeletionCleanup(CascadeAction action, Completion completion) {
        dbf.eoCleanup(VipVO.class);
        completion.success();
    }

    private void handleDeletion(CascadeAction action, final Completion completion) {
        final List<VipInventory> vipinvs = vipFromAction(action);
        if (vipinvs == null || vipinvs.isEmpty()) {
            completion.success();
            return;
        }

        List<VipDeletionMsg> msgs = CollectionUtils.transformToList(vipinvs, new Function<VipDeletionMsg, VipInventory>() {
            @Override
            public VipDeletionMsg call(VipInventory arg) {
                VipDeletionMsg msg = new VipDeletionMsg();
                msg.setVipUuid(arg.getUuid());
                bus.makeTargetServiceIdByResourceUuid(msg, VipConstant.SERVICE_ID, msg.getVipUuid());
                return msg;
            }
        });

        bus.send(msgs, 10, new CloudBusListCallBack(completion) {
            @Override
            public void run(List<MessageReply> replies) {
                for (MessageReply r : replies) {
                    VipInventory vip = vipinvs.get(replies.indexOf(r));
                    if (!r.isSuccess()) {
                        logger.warn(String.format("failed to delete vip[uuid:%s, ip: %s, name:%s], %s",
                                vip.getUuid(), vip.getIp(), vip.getName(), r.getError()));
                    }
                }

                completion.success();
            }
        });
    }

    private void handleDeletionCheck(CascadeAction action, Completion completion) {
        completion.success();
    }

    @Override
    public List<String> getEdgeNames() {
        return Arrays.asList(L3NetworkVO.class.getSimpleName(), IpRangeVO.class.getSimpleName());
    }

    @Override
    public String getCascadeResourceName() {
        return NAME;
    }

    @Override
    public CascadeAction createActionForChildResource(CascadeAction action) {
        if (CascadeConstant.DELETION_CODES.contains(action.getActionCode())) {
            List<VipInventory> vips = vipFromAction(action);
            if (vips != null) {
                return action.copy().setParentIssuer(NAME).setParentIssuerContext(vips);
            }
        }
        return null;
    }

    private List<VipInventory> vipFromAction(CascadeAction action) {
        if (L3NetworkVO.class.getSimpleName().equals(action.getParentIssuer())) {
            List<String> l3Uuids = CollectionUtils.transformToList((List<L3NetworkInventory>) action.getParentIssuerContext(), new Function<String, L3NetworkInventory>() {
                @Override
                public String call(L3NetworkInventory arg) {
                    return arg.getUuid();
                }
            });
            if (l3Uuids.isEmpty()) {
                return null;
            }

            SimpleQuery<VipVO> q = dbf.createQuery(VipVO.class);
            q.add(VipVO_.l3NetworkUuid, Op.IN, l3Uuids);
            List<VipVO> vipVOs = q.list();

            return VipInventory.valueOf(vipVOs);
        } else if (IpRangeVO.class.getSimpleName().equals(action.getParentIssuer())) {
            List<String> iprUuids = CollectionUtils.transformToList((List<IpRangeInventory>) action.getParentIssuerContext(), new Function<String, IpRangeInventory>() {
                @Override
                public String call(IpRangeInventory arg) {
                    return arg.getUuid();
                }
            });

            SimpleQuery<VipVO> q = dbf.createQuery(VipVO.class);
            q.add(VipVO_.ipRangeUuid, Op.IN, iprUuids);
            List<VipVO> vipVOs = q.list();

            return VipInventory.valueOf(vipVOs);
        } else if (VipVO.class.getSimpleName().equals(action.getParentIssuer())) {
            return action.getParentIssuerContext();
        }
        return null;
    }
}
