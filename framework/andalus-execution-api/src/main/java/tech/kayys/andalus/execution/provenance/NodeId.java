package tech.kayys.andalus.execution.provenance;

import java.util.Objects;
import java.util.UUID;

/**
 * Identifier for a node in the Temporal Execution Graph.
 */
public record NodeId(String value) {
    public NodeId {
        Objects.requireNonNull(value, "value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("value cannot be blank");
        }
    }

    public static NodeId of(String value) {
        return new NodeId(value);
    }

    public static NodeId generate() {
        return new NodeId("node-" + UUID.randomUUID());
    }
}
