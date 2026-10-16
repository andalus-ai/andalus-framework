package tech.kayys.andalus.harness.decision.governance;

/**
 * High-level outcome of evaluating a decision against governance constraints.
 */
public enum GovernanceOutcome {
    ALLOW,
    DENY,
    WAIT,
    REQUIRE_REVIEW,
    REQUIRE_OBLIGATION,
    DEFER,
    ERROR
}
