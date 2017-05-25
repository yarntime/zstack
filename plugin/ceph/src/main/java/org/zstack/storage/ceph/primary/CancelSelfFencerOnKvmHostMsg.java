package org.zstack.storage.ceph.primary;

import org.zstack.header.message.NeedReplyMessage;
import org.zstack.header.storage.primary.PrimaryStorageMessage;
import org.zstack.kvm.KvmSetupSelfFencerExtensionPoint.KvmCancelSelfFencerParam;

/**
 * Created by xing5 on 2016/5/10.
 */
public class CancelSelfFencerOnKvmHostMsg extends NeedReplyMessage implements PrimaryStorageMessage {
    private KvmCancelSelfFencerParam param;

    public KvmCancelSelfFencerParam getParam() {
        return param;
    }

    public void setParam(KvmCancelSelfFencerParam param) {
        this.param = param;
    }

    @Override
    public String getPrimaryStorageUuid() {
        return param.getPrimaryStorage().getUuid();
    }
}
