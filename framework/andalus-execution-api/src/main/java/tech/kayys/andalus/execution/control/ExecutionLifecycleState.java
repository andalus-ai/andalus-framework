package tech.kayys.andalus.execution.control;

/**
 * Rich authoritative state machine for Andalus Control Plane.
 */
public enum ExecutionLifecycleState {
    CREATED,
    ADMITTED,
    SCHEDULED,
    STARTING,
    RUNNING,
    PAUSING,
    PAUSED,
    CHECKPOINTING,
    CHECKPOINTED,
    MIGRATING,
    RESUMING,
    RECOVERING,
    COMPLETED,
    FAILED,
    CANCELLED,
    TERMINATED,
    QUARANTINED;

    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED || this == TERMINATED;
    }

    public boolean isPausedOrPausing() {
        return this == PAUSING || this == PAUSED;
    }
}
