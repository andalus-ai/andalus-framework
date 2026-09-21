package tech.kayys.andalus.harness.coordination.task;

/**
 * Operating states of a coordinated multi-agent task.
 */
public enum CoordinationTaskState {
    PENDING,
    ASSIGNED,
    RUNNING,
    COMPLETED,
    FAILED,
    CANCELED
}
