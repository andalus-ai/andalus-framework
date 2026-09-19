package tech.kayys.wayang.execution.lifecycle;

/**
 * Durable lifecycle states of a logical execution in Wayang v4.27.
 */
public enum ExecutionState {
    CREATED,
    ADMITTED,
    STARTING,
    RUNNING,
    CHECKPOINTING,
    PAUSED,
    RESUMING,
    COMPLETED,
    FAILED,
    CRASHED,
    LOST,
    CANCELLED,
    PREEMPTED,
    RECOVERABLE;

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED || (this == FAILED && !isRecoverable());
    }

    public boolean isRecoverable() {
        return this == CRASHED || this == LOST || this == PREEMPTED || this == RECOVERABLE;
    }
}
