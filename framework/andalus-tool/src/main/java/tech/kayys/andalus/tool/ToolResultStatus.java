package tech.kayys.andalus.tool;

/**
 * Status of a completed tool execution.
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
