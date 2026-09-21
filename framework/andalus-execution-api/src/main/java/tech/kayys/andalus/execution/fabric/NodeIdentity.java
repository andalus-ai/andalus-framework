package tech.kayys.andalus.execution.fabric;

import java.util.Objects;

/**
 * Stable cluster-wide identity of a Andalus execution node with epoch generation.
 */
public record NodeIdentity(
    String namespace,
    String nodeId,
    NodeGeneration generation
) {
    public NodeIdentity {
        Objects.requireNonNull(namespace, "namespace cannot be null");
        Objects.requireNonNull(nodeId, "nodeId cannot be null");
        Objects.requireNonNull(generation, "generation cannot be null");
    }

    public static NodeIdentity of(String namespace, String nodeId, long generation) {
        return new NodeIdentity(namespace, nodeId, NodeGeneration.of(generation));
    }

    public static NodeIdentity of(String nodeId) {
        return new NodeIdentity("default", nodeId, NodeGeneration.initial());
    }

    public String canonicalName() {
        return namespace + "/" + nodeId + "@" + generation.value();
    }
}
