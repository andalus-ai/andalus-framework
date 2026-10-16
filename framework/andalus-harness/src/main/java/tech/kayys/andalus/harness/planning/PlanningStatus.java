package tech.kayys.andalus.harness.planning;

/**
 * Outcome status of a planning or replanning operation.
 */
public enum PlanningStatus {
    /** Plan was successfully created or revised and passed validation. */
    SUCCEEDED,
    /** Plan was created (alias kept for compatibility). */
    CREATED,
    /** Plan was revised (alias kept for compatibility). */
    REVISED,
    /** No plan was necessary to satisfy the current goal. */
    NO_PLAN_NEEDED,
    /** Insufficient information to produce a plan. */
    INSUFFICIENT_INFORMATION,
    /** Constraints conflict — no valid plan is possible under current constraints. */
    CONSTRAINT_CONFLICT,
    /** Plan was produced but failed structural or domain validation. */
    INVALID,
    /** Planning operation failed due to an unexpected error. */
    FAILED
}
