package tech.kayys.andalus.execution.core.resource;

import tech.kayys.andalus.execution.resource.model.ResourceId;
import tech.kayys.andalus.execution.resource.model.ResourceLifetime;
import tech.kayys.andalus.execution.resource.model.ResourceScope;
import tech.kayys.andalus.execution.resource.model.ResourceType;
import tech.kayys.andalus.execution.resource.model.VirtualResource;

import java.util.Map;
import java.util.Objects;

/**
 * Default record implementation of {@link VirtualResource}.
 */
public record DefaultVirtualResource(
        ResourceId id,
        ResourceType type,
        ResourceScope scope,
        ResourceLifetime lifetime,
        Map<String, Object> attributes
) implements VirtualResource {
    public DefaultVirtualResource {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(type, "type cannot be null");
        if (scope == null) {
            scope = ResourceScope.EXECUTION;
        }
        if (lifetime == null) {
            lifetime = ResourceLifetime.TASK;
        }
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static DefaultVirtualResource of(ResourceId id, ResourceType type) {
        return new DefaultVirtualResource(id, type, ResourceScope.EXECUTION, ResourceLifetime.TASK, Map.of());
    }

    public static DefaultVirtualResource of(ResourceId id, ResourceType type, Map<String, Object> attributes) {
        return new DefaultVirtualResource(id, type, ResourceScope.EXECUTION, ResourceLifetime.TASK, attributes);
    }
}
