package tech.kayys.andalus.harness.state;

/**
 * Status outcome of a belief update operation.
 */
public enum BeliefUpdateStatus {
    CREATED,
    UPDATED,
    NO_CHANGE,
    CONFLICT,
    REJECTED,
    NOT_FOUND,
    FAILED;

    public boolean isSuccess() {
        return this == CREATED || this == UPDATED || this == NO_CHANGE;
    }
}
