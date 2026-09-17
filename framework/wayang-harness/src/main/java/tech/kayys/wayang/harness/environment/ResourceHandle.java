package tech.kayys.wayang.harness.environment;

import java.util.Map;
import java.util.Objects;

/**
 * Handle referencing an environmental resource (workspace, process sandbox, network socket, database).
 */
public record ResourceHandle(
        ResourceId id,
        String type,
        Map<String, Object> properties
) {

    public ResourceHandle {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(type, "type");
        properties = properties == null ? Map.of() : Map.copyOf(properties);
    }

    public static ResourceHandle of(String id, String type) {
        return new ResourceHandle(ResourceId.of(id), type, Map.of());
    }

    public static ResourceHandle of(String id, String type, Map<String, Object> properties) {
        return new ResourceHandle(ResourceId.of(id), type, properties);
    }
}
