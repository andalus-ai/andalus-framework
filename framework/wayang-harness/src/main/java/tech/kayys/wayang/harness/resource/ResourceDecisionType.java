package tech.kayys.wayang.harness.resource;

/**
 * Outcome of evaluating a resource request against a {@link ResourceScope}.
 */
public enum ResourceDecisionType {
    ALLOW,
    DENY,
    REQUIRE_APPROVAL
}
