package tech.kayys.wayang.state.lineage;

import java.util.Map;
import java.util.Objects;

public record LineageEdge(
        String sourceId,
        String targetId,
        String relation,
        Map<String, Object> attributes
) {
    public LineageEdge {
        Objects.requireNonNull(sourceId, "sourceId cannot be null");
        Objects.requireNonNull(targetId, "targetId cannot be null");
        relation = relation == null ? "DERIVED_FROM" : relation;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }
}
