package tech.kayys.wayang.execution.fabric;

/**
 * Operational state of an execution node.
 */
public enum NodeState {
    UP,
    DRAINING,
    DEGRADED,
    DOWN,
    MAINTENANCE
}
