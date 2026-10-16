package tech.kayys.andalus.harness.planning;

/**
 * Lifecycle states of an agent plan.
 */
public enum PlanStatus {
    PROPOSED,
    ACTIVE,
    PAUSED,
    COMPLETED,
    FAILED,
    CANCELLED,
    SUPERSEDED;

    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED || this == SUPERSEDED;
    }
}
