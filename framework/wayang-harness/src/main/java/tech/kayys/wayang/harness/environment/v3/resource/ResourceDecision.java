package tech.kayys.wayang.harness.environment.v3.resource;

import java.util.Objects;

/**
 * Outcome of evaluating a resource policy against a resource request.
 */
public record ResourceDecision(
        DecisionType type,
        String reason,
        LeaseConstraints limits
) {
    /**
     * Enumerates the decision type values used by the Wayang framework.
     */

    public enum DecisionType {
        ALLOW,
        DENY,
        ALLOW_WITH_LIMITS,
        REQUIRE_APPROVAL,
        QUEUE
    }

    public ResourceDecision {
        Objects.requireNonNull(type, "type");
        reason = reason != null ? reason : "";
        limits = limits != null ? limits : LeaseConstraints.unconstrained();
    }

    public static ResourceDecision allow() {
        return new ResourceDecision(DecisionType.ALLOW, "Resource access permitted", LeaseConstraints.unconstrained());
    }

    public static ResourceDecision deny(String reason) {
        return new ResourceDecision(DecisionType.DENY, reason, LeaseConstraints.unconstrained());
    }

    public static ResourceDecision allowWithLimits(String reason, LeaseConstraints limits) {
        return new ResourceDecision(DecisionType.ALLOW_WITH_LIMITS, reason, limits);
    }

    public static ResourceDecision requireApproval(String reason) {
        return new ResourceDecision(DecisionType.REQUIRE_APPROVAL, reason, LeaseConstraints.unconstrained());
    }

    public static ResourceDecision queue(String reason) {
        return new ResourceDecision(DecisionType.QUEUE, reason, LeaseConstraints.unconstrained());
    }
}
