package tech.kayys.andalus.harness.learning;

/**
 * Outcome status of ingesting a learning signal.
 */
public enum LearningSignalIngestionStatus {
    ACCEPTED,
    DUPLICATE,
    REJECTED,
    QUARANTINED,
    FAILED;

    public boolean isSuccess() {
        return this == ACCEPTED || this == DUPLICATE;
    }
}
