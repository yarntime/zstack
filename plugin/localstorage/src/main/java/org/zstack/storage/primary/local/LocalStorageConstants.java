package org.zstack.storage.primary.local;

import org.zstack.header.configuration.PythonClass;

/**
 * Created by frank on 6/30/2015.
 */
@PythonClass
public class LocalStorageConstants {
    @PythonClass
    public static final String LOCAL_STORAGE_TYPE = "LocalStorage";
    public static final String LOCAL_STORAGE_ALLOCATOR_STRATEGY = "LocalPrimaryStorageStrategy";
    public static final String LOCAL_STORAGE_MIGRATE_VM_ALLOCATOR_TYPE = "LocalStorageMigrateVmAllocatorStrategy";
}
