package tech.kayys.wayang.harness.workflow;

import java.util.Map;
import java.util.Objects;

/**
 * Descriptive metadata for a node.
 */
public record NodeMetadata(
        String label,
        String description,
        Map<String, Object> attributes
) {

    public NodeMetadata {
        label = label != null ? label : "";
        description = description != null ? description : "";
        attributes = attributes != null ? Map.copyOf(attributes) : Map.of();
    }

    public static NodeMetadata of(String label) {
        return new NodeMetadata(label, "", Map.of());
    }

    public static NodeMetadata of(String label, String description) {
        return new NodeMetadata(label, description, Map.of());
    }
}
