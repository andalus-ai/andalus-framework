package tech.kayys.wayang.workflow.graph;

import java.util.Set;

/**
 * Computes eligible nodes ready for admission and execution alongside blocked nodes.
 */
public record ReadySet(
        Set<ExecutionNode> readyNodes,
        Set<NodeId> blockedNodeIds
) {

    public ReadySet {
        readyNodes = readyNodes != null ? Set.copyOf(readyNodes) : Set.of();
        blockedNodeIds = blockedNodeIds != null ? Set.copyOf(blockedNodeIds) : Set.of();
    }

    public static ReadySet of(Set<ExecutionNode> ready, Set<NodeId> blocked) {
        return new ReadySet(ready, blocked);
    }

    public static ReadySet empty() {
        return new ReadySet(Set.of(), Set.of());
    }

    public boolean hasReadyNodes() {
        return !readyNodes.isEmpty();
    }
}
