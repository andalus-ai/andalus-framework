package tech.kayys.andalus.memory.graph;

import java.io.Serializable;
import java.util.Objects;

/**
 * A directed edge representing semantic relationships between memory nodes.
 */
public record MemoryEdge(
    String sourceNodeId,
    String targetNodeId,
    String relationship,
    double weight
) implements Serializable {

    public MemoryEdge {
        Objects.requireNonNull(sourceNodeId, "sourceNodeId cannot be null");
        Objects.requireNonNull(targetNodeId, "targetNodeId cannot be null");
        Objects.requireNonNull(relationship, "relationship cannot be null");
    }

    public static MemoryEdge of(String sourceNodeId, String targetNodeId, String relationship) {
        return new MemoryEdge(sourceNodeId, targetNodeId, relationship, 1.0);
    }
}
