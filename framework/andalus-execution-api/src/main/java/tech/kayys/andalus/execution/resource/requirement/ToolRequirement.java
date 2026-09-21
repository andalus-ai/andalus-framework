package tech.kayys.andalus.execution.resource.requirement;

import tech.kayys.andalus.execution.resource.model.ResourceId;
import tech.kayys.andalus.execution.resource.model.ResourceType;

import java.util.Objects;

/**
 * Tool capability requirement specification.
 */
public record ToolRequirement(
        ResourceId resourceId,
        String toolName,
        String versionRange
) implements ResourceRequirement {
    public ToolRequirement {
        Objects.requireNonNull(resourceId, "resourceId cannot be null");
        Objects.requireNonNull(toolName, "toolName cannot be null");
        versionRange = versionRange == null ? "any" : versionRange;
    }

    @Override
    public ResourceType type() {
        return ResourceType.TOOL;
    }

    public static ToolRequirement of(ResourceId id, String name) {
        return new ToolRequirement(id, name, "any");
    }
}
