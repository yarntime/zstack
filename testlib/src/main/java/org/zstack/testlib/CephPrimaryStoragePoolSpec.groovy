package org.zstack.testlib

import org.zstack.sdk.CephPrimaryStoragePoolInventory


/**
 * Created by xing5 on 2017/2/28.
 */
class CephPrimaryStoragePoolSpec extends Spec {
    @SpecParam(required = true)
    String poolName
    @SpecParam
    String description

    CephPrimaryStoragePoolInventory inventory

    CephPrimaryStoragePoolSpec(EnvSpec envSpec) {
        super(envSpec)
    }

    @Override
    void delete(String sessionId) {
        deleteCephPrimaryStoragePool {
            uuid = inventory.uuid
            delegate.sessionId = sessionId
        }
    }

    @Override
    SpecID create(String uuid, String sessionId) {

        inventory = addCephPrimaryStoragePool {
            delegate.poolName = poolName
            delegate.primaryStorageUuid = (parent as PrimaryStorageSpec).inventory.uuid
            delegate.description = description
            delegate.systemTags = systemTags
            delegate.userTags = userTags
            delegate.resourceUuid = uuid
            delegate.sessionId = sessionId
        }

        return id(poolName, inventory.uuid)
    }
}
