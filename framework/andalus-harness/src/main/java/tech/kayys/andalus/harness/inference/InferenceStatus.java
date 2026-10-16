package tech.kayys.andalus.harness.inference;

/**
 * Status outcomes for an inference operation.
 */
public enum InferenceStatus {
    REQUEST_INVALID,
    CAPABILITY_UNAVAILABLE,
    MODEL_UNAVAILABLE,
    PROVIDER_UNAVAILABLE,
    TIMEOUT,
    RATE_LIMITED,
    BUDGET_EXCEEDED,
    CANCELLED,
    INVALID_OUTPUT,
    FAILED,
    COMPLETED;

    public boolean isTerminal() {
        return this != REQUEST_INVALID;
    }

    public boolean isSuccess() {
        return this == COMPLETED;
    }
}
