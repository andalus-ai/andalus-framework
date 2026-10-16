package tech.kayys.andalus.harness.state;

/**
 * Status lifecycle of an agent belief.
 */
public enum BeliefStatus {
    PROPOSED,
    ACTIVE,
    DISPUTED,
    UNRESOLVED,
    RETRACTED,
    SUPERSEDED;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isTerminal() {
        return this == RETRACTED || this == SUPERSEDED;
    }
}
