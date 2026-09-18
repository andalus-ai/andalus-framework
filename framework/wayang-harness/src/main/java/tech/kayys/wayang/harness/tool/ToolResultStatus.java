package tech.kayys.wayang.harness.tool;

/**
 * Defines the tool result status values used by the Wayang framework.
 */


public enum ToolResultStatus {
    SUCCESS,
    FAILURE,
    TIMEOUT,
    CANCELED,
    DENIED,
    REQUIRES_APPROVAL,
    UNAVAILABLE
}
