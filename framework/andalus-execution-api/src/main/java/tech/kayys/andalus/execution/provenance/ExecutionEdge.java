package tech.kayys.andalus.execution.provenance;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * A directed causal or structural edge in the Temporal Execution Graph.
 */
public record ExecutionEdge(
        NodeId source,
        NodeId target,
        EdgeType type,
        Instant timestamp,
        Map<String, Object> attributes
) {
    public ExecutionEdge {
        Objects.requireNonNull(source, "source cannot be null");
        Objects.requireNonNull(target, "target cannot be null");
        Objects.requireNonNull(type, "type cannot be null");
        if (timestamp == null) {
            timestamp = Instant.now();
        }
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static ExecutionEdge of(NodeId source, NodeId target, EdgeType type) {
        return new ExecutionEdge(source, target, type, Instant.now(), Map.of());
    }
}
