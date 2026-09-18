package tech.kayys.wayang.harness.tool;

/**
 * Defines the tool execution status values used by the Wayang framework.
 */


public enum ToolExecutionStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    FAILED,
    CANCELED,
    TIMED_OUT
}
