package tech.kayys.wayang.harness.execution.state;

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
