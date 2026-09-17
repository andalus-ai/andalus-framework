package tech.kayys.wayang.harness.resource;

import java.util.Objects;

/**
 * Represents the evaluation decision for a resource request.
 */
public record ResourceDecision(
        ResourceDecisionType type,
        String reason,
        String policyId
) {

    public ResourceDecision {
        Objects.requireNonNull(type, "type");
        reason = reason == null ? "" : reason;
        policyId = policyId == null ? "default" : policyId;
    }

    public boolean isAllowed() {
        return type == ResourceDecisionType.ALLOW;
    }

    public boolean isDenied() {
        return type == ResourceDecisionType.DENY;
    }

    public boolean requiresApproval() {
        return type == ResourceDecisionType.REQUIRE_APPROVAL;
    }

    public static ResourceDecision allow() {
        return new ResourceDecision(ResourceDecisionType.ALLOW, "Allowed by policy", "default");
    }

    public static ResourceDecision allow(String reason) {
        return new ResourceDecision(ResourceDecisionType.ALLOW, reason, "default");
    }

    public static ResourceDecision deny(String reason) {
        return new ResourceDecision(ResourceDecisionType.DENY, reason, "default");
    }

    public static ResourceDecision deny(String policyId, String reason) {
        return new ResourceDecision(ResourceDecisionType.DENY, reason, policyId);
    }

    public static ResourceDecision requireApproval(String reason) {
        return new ResourceDecision(ResourceDecisionType.REQUIRE_APPROVAL, reason, "approval-required");
    }

    public static ResourceDecision requireApproval(String policyId, String reason) {
        return new ResourceDecision(ResourceDecisionType.REQUIRE_APPROVAL, reason, policyId);
    }
}
