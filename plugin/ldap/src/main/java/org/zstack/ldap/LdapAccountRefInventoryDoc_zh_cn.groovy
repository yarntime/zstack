package org.zstack.ldap

doc {

    title "LDAP账户绑定关系清单"

    field {
        name "uuid"
        desc "资源的UUID，唯一标示该资源"
        type "String"
        since "0.6"
    }
    field {
        name "ldapUid"
        desc "LDAP登录使用的UID"
        type "String"
        since "0.6"
    }
    field {
        name "ldapServerUuid"
        desc "LDAP服务器UUID"
        type "String"
        since "0.6"
    }
    field {
        name "accountUuid"
        desc "账户UUID"
        type "String"
        since "0.6"
    }
    field {
        name "createDate"
        desc "创建时间"
        type "Timestamp"
        since "0.6"
    }
    field {
        name "lastOpDate"
        desc "最后一次修改时间"
        type "Timestamp"
        since "0.6"
    }
}
