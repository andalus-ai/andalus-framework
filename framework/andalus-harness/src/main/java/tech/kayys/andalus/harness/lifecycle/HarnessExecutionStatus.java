package tech.kayys.andalus.harness.lifecycle;

/**
 * Finite lifecycle state vocabulary for an agent execution within Andalus Harness.
 */
public enum HarnessExecutionStatus {
    CREATED,
    ADMITTED,
    INITIALIZING,
    RUNNING,
    WAITING,
    SUSPENDED,
    RESUMING,
    COMPLETED,
    FAILED,
    CANCELLED,
    EXPIRED;

    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED || this == EXPIRED;
    }
}
