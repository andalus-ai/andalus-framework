package tech.kayys.wayang.execution.resource.requirement;

import tech.kayys.wayang.execution.resource.model.ResourceId;
import tech.kayys.wayang.execution.resource.model.ResourceType;

import java.util.Objects;

/**
 * CPU and compute requirement specification.
 */
public record ComputeRequirement(
        ResourceId resourceId,
        double minCpus,
        double maxCpus,
        String arch
) implements ResourceRequirement {
    public ComputeRequirement {
        Objects.requireNonNull(resourceId, "resourceId cannot be null");
        if (minCpus < 0 || maxCpus < minCpus) {
            throw new IllegalArgumentException("Invalid CPU bounds: min=" + minCpus + ", max=" + maxCpus);
        }
        arch = arch == null ? "any" : arch;
    }

    @Override
    public ResourceType type() {
        return ResourceType.COMPUTE;
    }

    public static ComputeRequirement of(ResourceId id, double minCpus, double maxCpus) {
        return new ComputeRequirement(id, minCpus, maxCpus, "any");
    }
}
