package tech.kayys.andalus.tool;

/**
 * Lifecycle status of a scheduled tool execution.
 */
public enum ToolExecutionStatus {
    SCHEDULED,
    RUNNING,
    COMPLETED,
    FAILED,
    TIMED_OUT,
    CANCELLED,
    CANCELED
}
