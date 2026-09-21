package tech.kayys.andalus.execution.provenance;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * An immutable node in the Temporal Execution Graph.
 */
public record ExecutionNode(
        NodeId id,
        NodeType type,
        String executionId,
        Instant occurredAt,
        Map<String, Object> attributes
) {
    public ExecutionNode {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(executionId, "executionId cannot be null");
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static ExecutionNode of(NodeId id, NodeType type, String executionId) {
        return new ExecutionNode(id, type, executionId, Instant.now(), Map.of());
    }

    public static ExecutionNode of(NodeType type, String executionId, Map<String, Object> attributes) {
        return new ExecutionNode(NodeId.generate(), type, executionId, Instant.now(), attributes);
    }
}
