package tech.kayys.wayang.execution.resource.requirement;

import tech.kayys.wayang.execution.resource.model.ResourceId;
import tech.kayys.wayang.execution.resource.model.ResourceType;

import java.util.Objects;

/**
 * Storage and disk space requirement specification.
 */
public record StorageRequirement(
        ResourceId resourceId,
        long capacityBytes,
        boolean persistent,
        String mountPath
) implements ResourceRequirement {
    public StorageRequirement {
        Objects.requireNonNull(resourceId, "resourceId cannot be null");
        if (capacityBytes <= 0) {
            throw new IllegalArgumentException("capacityBytes must be positive");
        }
        mountPath = mountPath == null ? "/workspace" : mountPath;
    }

    @Override
    public ResourceType type() {
        return ResourceType.STORAGE;
    }

    public static StorageRequirement gigabytes(ResourceId id, long gb, boolean persistent) {
        return new StorageRequirement(id, gb * 1024L * 1024L * 1024L, persistent, "/workspace");
    }
}
