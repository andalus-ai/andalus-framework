package tech.kayys.andalus.harness.model;

/**
 * Defines the model result status values used by the Andalus framework.
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
