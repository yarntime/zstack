package org.zstack.storage.primary.nfs;

import org.zstack.core.config.GlobalConfig;
import org.zstack.core.config.GlobalConfigDefinition;
import org.zstack.core.config.GlobalConfigValidation;

/**
 */
@GlobalConfigDefinition
public class NfsPrimaryStorageGlobalConfig {
    public static final String CATEGORY = "nfsPrimaryStorage";

    @GlobalConfigValidation
    public static GlobalConfig MOUNT_BASE = new GlobalConfig(CATEGORY, "mount.base");

    @GlobalConfigValidation
    public static GlobalConfig GC_INTERVAL = new GlobalConfig(CATEGORY, "deletion.gcInterval");
}
