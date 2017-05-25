package org.zstack.header.identity;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(AccountResourceRefVO.class)
public class AccountResourceRefVO_ {
    public static volatile SingularAttribute<AccountResourceRefVO, Long> id;
    public static volatile SingularAttribute<AccountResourceRefVO, String> accountUuid;
    public static volatile SingularAttribute<AccountResourceRefVO, String> ownerAccountUuid;
    public static volatile SingularAttribute<AccountResourceRefVO, String> resourceUuid;
    public static volatile SingularAttribute<AccountResourceRefVO, String> resourceType;
    public static volatile SingularAttribute<AccountResourceRefVO, Integer> permission;
    public static volatile SingularAttribute<AccountResourceRefVO, Boolean> isShared;
    public static volatile SingularAttribute<AccountResourceRefVO, Timestamp> createDate;
    public static volatile SingularAttribute<AccountResourceRefVO, Timestamp> lastOpDate;
}
