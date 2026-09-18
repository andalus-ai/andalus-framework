package tech.kayys.wayang.harness.execution.state;

/**
 * Defines the execution status values used by the Wayang framework.
 */


public enum ExecutionStatus {
    CREATED,
    RUNNING,
    WAITING,
    SUSPENDED,
    RECOVERING,
    COMPLETED,
    FAILED,
    CANCELED
}
