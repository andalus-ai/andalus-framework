package tech.kayys.wayang.harness.workflow;

/**
 * Execution lifecycle states of an individual node in the graph.
 */
public enum NodeState {
    PENDING,
    READY,
    ADMITTED,
    RUNNING,
    COMPLETED,
    FAILED,
    CANCELLED,
    BLOCKED,
    SKIPPED,
    WAITING;

    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED || this == SKIPPED;
    }
}
