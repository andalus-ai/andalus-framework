package tech.kayys.andalus.execution.fabric;

import java.time.Instant;
import java.util.Objects;

/**
 * Dynamic health and availability status of a node.
 */
public record NodeStatus(
    NodeState state,
    Instant lastHeartbeat,
    double loadFactor,
    boolean acceptingTasks
) {
    public NodeStatus {
        Objects.requireNonNull(state, "state cannot be null");
        Objects.requireNonNull(lastHeartbeat, "lastHeartbeat cannot be null");
    }

    public static NodeStatus healthy(double load) {
        return new NodeStatus(NodeState.UP, Instant.now(), load, true);
    }

    public static NodeStatus down() {
        return new NodeStatus(NodeState.DOWN, Instant.now(), 1.0, false);
    }

    public boolean isAvailable() {
        return state == NodeState.UP && acceptingTasks;
    }
}
