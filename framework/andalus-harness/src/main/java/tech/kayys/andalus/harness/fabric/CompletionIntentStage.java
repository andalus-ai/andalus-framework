package tech.kayys.andalus.harness.fabric;

/** Monotonic progress markers for a cross-store completion operation. */
public enum CompletionIntentStage {
    RECORDED,
    EXECUTION_STATE_COMMITTED,
    ATTEMPT_COMMITTED,
    COMPLETED
}
