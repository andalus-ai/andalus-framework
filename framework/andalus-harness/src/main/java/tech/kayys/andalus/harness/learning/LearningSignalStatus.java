package tech.kayys.andalus.harness.learning;

/**
 * Lifecycle status of a learning signal record.
 */
public enum LearningSignalStatus {
    RECEIVED,
    VALIDATED,
    ACCEPTED,
    REJECTED,
    QUARANTINED,
    EXPIRED
}
