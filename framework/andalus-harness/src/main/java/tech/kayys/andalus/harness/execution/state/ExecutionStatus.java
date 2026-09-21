package tech.kayys.andalus.harness.execution.state;

/**
 * Defines the execution status values used by the Andalus framework.
 */


public enum ExecutionStatus {
    CREATED,
    RUNNING,
    WAITING,
    SUSPENDED,
    RECOVERING,
    COMPLETED,
    FAILED,
    CANCELED,
    TIMEOUT;

    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == CANCELED || this == TIMEOUT;
    }
}
