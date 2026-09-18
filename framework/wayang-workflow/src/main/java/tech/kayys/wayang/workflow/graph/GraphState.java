package tech.kayys.wayang.workflow.graph;

/**
 * Execution lifecycle states of an overall execution graph.
 */
public enum GraphState {
    DRAFT,
    VALIDATING,
    READY,
    RUNNING,
    PAUSED,
    WAITING_APPROVAL,
    COMPLETED,
    FAILED,
    CANCELLED,
    ABORTED;

    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED || this == ABORTED;
    }
}
