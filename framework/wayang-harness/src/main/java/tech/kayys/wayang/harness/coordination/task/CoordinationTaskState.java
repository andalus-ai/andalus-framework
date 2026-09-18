package tech.kayys.wayang.harness.coordination.task;

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
