package tech.kayys.andalus.harness.reflection;

/**
 * Outcome status of a reflection operation.
 */
public enum ReflectionStatus {
    COMPLETED,
    PARTIAL,
    INSUFFICIENT_EVIDENCE,
    SKIPPED,
    FAILED,
    CANCELLED;

    public boolean isSuccess() {
        return this == COMPLETED || this == PARTIAL;
    }
}
