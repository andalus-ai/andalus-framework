package tech.kayys.wayang.harness.scheduling.retry;

/**
 * Classification of failures regarding their retryability.
 */
public enum Retryability {
    RETRYABLE,
    NON_RETRYABLE,
    UNKNOWN
}
