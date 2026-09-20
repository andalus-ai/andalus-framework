package tech.kayys.wayang.execution.mobility;

/**
 * Execution status for execution mobility in Wayang v4.32.
 */
public enum ExecutionStatus {
    CREATED,
    ADMITTED,
    RUNNING,
    CHECKPOINTING,
    SUSPENDED,
    MIGRATING,
    RESUMING,
    COMPLETED,
    FAILED,
    RECOVERING,
    CANCELLED;

    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED;
    }

    public boolean isMovable() {
        return this == SUSPENDED || this == RUNNING || this == CHECKPOINTING;
    }
}
