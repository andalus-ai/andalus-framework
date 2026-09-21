package tech.kayys.andalus.harness.scheduling.retry;

/**
 * Classification of failures regarding their retryability.
 */
public enum Retryability {
    RETRYABLE,
    NON_RETRYABLE,
    UNKNOWN
}
