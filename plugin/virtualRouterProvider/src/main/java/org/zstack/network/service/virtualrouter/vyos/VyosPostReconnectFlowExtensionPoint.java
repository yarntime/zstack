package org.zstack.network.service.virtualrouter.vyos;

import org.zstack.header.core.workflow.Flow;

/**
 * Created by xing5 on 2016/11/8.
 */
public interface VyosPostReconnectFlowExtensionPoint {
    Flow vyosPostReconnectFlow();
}
