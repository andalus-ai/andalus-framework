package tech.kayys.wayang.harness.workflow;

import java.util.Objects;

/**
 * References a specific output slot or attribute of a node.
 */
public record DataReference(NodeId nodeId, String outputKey) {

    public DataReference {
        Objects.requireNonNull(nodeId, "NodeId cannot be null");
        outputKey = outputKey != null ? outputKey : "default";
    }

    public static DataReference of(NodeId nodeId) {
        return new DataReference(nodeId, "default");
    }

    public static DataReference of(NodeId nodeId, String outputKey) {
        return new DataReference(nodeId, outputKey);
    }
}
