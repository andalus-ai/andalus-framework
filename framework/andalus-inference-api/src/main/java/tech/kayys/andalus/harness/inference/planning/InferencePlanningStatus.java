package tech.kayys.andalus.harness.inference.planning;

/**
 * Outcome status of an inference planning operation.
 */
public enum InferencePlanningStatus {
    PLANNED,
    NO_FEASIBLE_CANDIDATE,
    CAPABILITY_UNAVAILABLE,
    MODEL_UNAVAILABLE,
    BUDGET_EXCEEDED,
    CONSTRAINT_CONFLICT,
    PROVIDER_UNAVAILABLE,
    FAILED;

    public boolean isSuccess() {
        return this == PLANNED;
    }
}
