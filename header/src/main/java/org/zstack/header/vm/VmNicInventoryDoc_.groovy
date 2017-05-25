package org.zstack.header.vm

doc {

    title "在这里输入结构的名称"

    field {
        name "uuid"
        desc "资源的UUID，唯一标示该资源"
        type "String"
        since "0.6"
    }
    field {
        name "vmInstanceUuid"
        desc "云主机UUID"
        type "String"
        since "0.6"
    }
    field {
        name "l3NetworkUuid"
        desc "三层网络UUID"
        type "String"
        since "0.6"
    }
    field {
        name "ip"
        desc ""
        type "String"
        since "0.6"
    }
    field {
        name "mac"
        desc ""
        type "String"
        since "0.6"
    }
    field {
        name "netmask"
        desc ""
        type "String"
        since "0.6"
    }
    field {
        name "gateway"
        desc ""
        type "String"
        since "0.6"
    }
    field {
        name "metaData"
        desc ""
        type "String"
        since "0.6"
    }
    field {
        name "deviceId"
        desc ""
        type "Integer"
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
