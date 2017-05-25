package org.zstack.storage.primary;

import org.zstack.header.storage.primary.PrimaryStorageVO;
import org.zstack.header.tag.TagDefinition;
import org.zstack.tag.PatternedSystemTag;
import org.zstack.tag.SystemTag;

/**
 */
@TagDefinition
public class PrimaryStorageSystemTags {
    public static SystemTag CAPABILITY_SNAPSHOT = new SystemTag("capability:snapshot", PrimaryStorageVO.class);

    public static final String PRIMARY_STORAGE_ALLOCATOR_UUID_TAG_TOKEN = "uuid";
    public static PatternedSystemTag PRIMARY_STORAGE_ALLOCATOR_UUID_TAG = new PatternedSystemTag(String.format("primaryStorage::allocator::uuid::{%s}", PRIMARY_STORAGE_ALLOCATOR_UUID_TAG_TOKEN), PrimaryStorageVO.class);
    public static PatternedSystemTag PRIMARY_STORAGE_ALLOCATOR_USERTAG_TAG = new PatternedSystemTag("primaryStorage::allocator::userTag::{tag}", PrimaryStorageVO.class);
    public static PatternedSystemTag PRIMARY_STORAGE_ALLOCATOR_USERTAG_TAG_MANDATORY = new PatternedSystemTag("primaryStorage::allocator::userTag::{tag}::required", PrimaryStorageVO.class);

    public static final String CAPABILITY_HYPERVISOR_SNAPSHOT_TOKEN= "hypervisorType";
    public static PatternedSystemTag CAPABILITY_HYPERVISOR_SNAPSHOT = new PatternedSystemTag(String.format("capability::snapshot::hypervisor::{%s}", CAPABILITY_HYPERVISOR_SNAPSHOT_TOKEN), PrimaryStorageVO.class);
}
