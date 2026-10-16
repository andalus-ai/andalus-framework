package tech.kayys.andalus.harness.goal;

/**
 * Observed lifecycle states of an agent goal.
 */
public enum GoalState {
    PROPOSED,
    ACTIVE,
    BLOCKED,
    SATISFIED,
    FAILED,
    CANCELLED,
    SUPERSEDED;

    public boolean isTerminal() {
        return this == SATISFIED || this == FAILED || this == CANCELLED || this == SUPERSEDED;
    }
}
