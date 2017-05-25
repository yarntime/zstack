package org.zstack.kvm;

import org.zstack.header.message.MessageReply;
import org.zstack.header.message.NoJsonSchema;
import org.zstack.utils.gson.JSONObjectUtil;

/**
 */
public class KVMHostSyncHttpCallReply extends MessageReply {
    @NoJsonSchema
    private Object response;

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public <T> T toResponse(Class<T> clz) {
        return JSONObjectUtil.rehashObject(response, clz);
    }
}
