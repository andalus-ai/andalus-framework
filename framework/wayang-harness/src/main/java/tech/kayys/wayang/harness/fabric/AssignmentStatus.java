package tech.kayys.wayang.harness.fabric;

/**
 * Status lifecycle of a worker assignment.
 */
public enum AssignmentStatus {
    PENDING,
    DISPATCHED,
    RUNNING,
    COMPLETED,
    FAILED,
    TIMED_OUT,
    CANCELLED;

    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == TIMED_OUT || this == CANCELLED;
    }
}
