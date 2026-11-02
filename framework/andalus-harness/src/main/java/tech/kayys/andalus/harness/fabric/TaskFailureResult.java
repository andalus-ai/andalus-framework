package tech.kayys.andalus.harness.fabric;

/** Atomic result of applying a failure disposition to a current queue claim. */
public enum TaskFailureResult {
    RETRY_SCHEDULED,
    DEAD_LETTERED,
    LOST_CLAIM
}
