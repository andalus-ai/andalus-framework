package tech.kayys.wayang.state.lineage;

import java.util.Map;
import java.util.Objects;

public record LineageNode(
        String id,
        String type,
        String label,
        Map<String, Object> attributes
) {
    public LineageNode {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(type, "type cannot be null");
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }
}
