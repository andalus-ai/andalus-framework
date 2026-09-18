package tech.kayys.wayang.harness.consistency.state;

/**
 * Explicit execution lifecycle states in the Wayang Harness.
 */
public enum ExecutionState {
    CREATED,
    ADMITTED,
    INITIALIZING,
    READY,
    RUNNING,
    CHECKPOINTING,
    PAUSED,
    COMPLETING,
    COMMITTED,
    FAILED,
    RETRYING,
    CANCEL_REQUESTED,
    CANCELING,
    CANCELLED,
    TERMINAL_FAILURE;

    public boolean isTerminal() {
        return this == COMMITTED || this == CANCELLED || this == TERMINAL_FAILURE;
    }
}
