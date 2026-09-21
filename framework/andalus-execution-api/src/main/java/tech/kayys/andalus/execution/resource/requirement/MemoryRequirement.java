package tech.kayys.andalus.execution.resource.requirement;

import tech.kayys.andalus.execution.resource.model.ResourceId;
import tech.kayys.andalus.execution.resource.model.ResourceType;

import java.util.Objects;

/**
 * RAM/Memory requirement specification.
 */
public record MemoryRequirement(
        ResourceId resourceId,
        long minBytes,
        long maxBytes
) implements ResourceRequirement {
    public MemoryRequirement {
        Objects.requireNonNull(resourceId, "resourceId cannot be null");
        if (minBytes < 0 || maxBytes < minBytes) {
            throw new IllegalArgumentException("Invalid memory bounds: min=" + minBytes + ", max=" + maxBytes);
        }
    }

    @Override
    public ResourceType type() {
        return ResourceType.MEMORY;
    }

    public static MemoryRequirement megabytes(ResourceId id, long minMb, long maxMb) {
        return new MemoryRequirement(id, minMb * 1024L * 1024L, maxMb * 1024L * 1024L);
    }
}
