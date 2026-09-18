package tech.kayys.wayang.harness.environment.v3.resource;

import java.util.Map;
import java.util.Objects;

/**
 * Metadata descriptor for an environmental resource.
 */
public record ResourceDescriptor(
        ResourceId id,
        String name,
        ResourceType type,
        Map<String, String> attributes,
        long capacity
) {
    public ResourceDescriptor {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(type, "type");
        attributes = attributes != null ? Map.copyOf(attributes) : Map.of();
    }

    public static ResourceDescriptor of(ResourceId id, String name, ResourceType type) {
        return new ResourceDescriptor(id, name, type, Map.of(), 1L);
    }
}
