package tech.kayys.andalus.harness.planning;

/**
 * Controls how the planning runtime handles completed steps omitted or changed
 * by a proposed revision.
 */
public enum PlanRevisionPolicy {

    /** Reject revisions that remove or modify steps reported as successful. */
    PRESERVE_COMPLETED_STEPS,

    /**
     * Allow a planner to replace completed steps when requirements or external
     * constraints make the previous work obsolete. Structural plan validation
     * and plan identity/revision checks still apply.
     */
    ALLOW_COMPLETED_STEP_REPLACEMENT
}
