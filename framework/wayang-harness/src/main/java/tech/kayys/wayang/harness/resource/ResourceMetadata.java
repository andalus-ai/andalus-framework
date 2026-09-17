package tech.kayys.wayang.harness.resource;

import java.util.Map;

/**
 * Descriptive metadata and provider provenance for a resource.
 */
public record ResourceMetadata(
        String providerId,
        Map<String, String> labels,
        Map<String, Object> properties
) {

    public ResourceMetadata {
        providerId = providerId == null ? "default" : providerId;
        labels = labels == null ? Map.of() : Map.copyOf(labels);
        properties = properties == null ? Map.of() : Map.copyOf(properties);
    }

    public static ResourceMetadata of(String providerId) {
        return new ResourceMetadata(providerId, Map.of(), Map.of());
    }

    public static ResourceMetadata empty() {
        return new ResourceMetadata("default", Map.of(), Map.of());
    }
}
