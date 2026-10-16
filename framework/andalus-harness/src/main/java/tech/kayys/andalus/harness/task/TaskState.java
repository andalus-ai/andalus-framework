package tech.kayys.andalus.harness.task;

/**
 * Observed lifecycle states of an agent task.
 */
public enum TaskState {
    CREATED,
    READY,
    RUNNING,
    WAITING,
    BLOCKED,
    COMPLETED,
    FAILED,
    CANCELLED,
    SKIPPED;

    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED || this == SKIPPED;
    }
}
