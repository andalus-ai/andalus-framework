package tech.kayys.wayang.harness.model;

/**
 * Defines the model result status values used by the Wayang framework.
 */


public enum ModelResultStatus {
    SUCCESS,
    FAILURE,
    TIMEOUT,
    RATE_LIMITED,
    CONTEXT_EXCEEDED,
    CANCELLED,
    DENIED
}
