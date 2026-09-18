package tech.kayys.wayang.workflow.graph;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique identifier for a node within an execution graph.
 */
public record NodeId(String value) {

    public NodeId {
        Objects.requireNonNull(value, "NodeId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("NodeId value cannot be blank");
        }
    }

    public static NodeId of(String value) {
        return new NodeId(value);
    }

    public static NodeId generate() {
        return new NodeId("node-" + UUID.randomUUID());
    }
}
