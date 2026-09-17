package tech.kayys.wayang.harness.capability;

import java.util.Objects;

/**
 * Result of evaluating whether an execution may invoke a capability.
 */
public record CapabilityDecision(
        CapabilityDecisionType type,
        String reason,
        String policyId
) {

    public CapabilityDecision {
        Objects.requireNonNull(type, "type");
    }

    public static CapabilityDecision allow() {
        return new CapabilityDecision(CapabilityDecisionType.ALLOW, null, null);
    }

    public static CapabilityDecision allow(String policyId) {
        return new CapabilityDecision(CapabilityDecisionType.ALLOW, null, policyId);
    }

    public static CapabilityDecision deny(String reason) {
        return new CapabilityDecision(CapabilityDecisionType.DENY, reason, null);
    }

    public static CapabilityDecision deny(String reason, String policyId) {
        return new CapabilityDecision(CapabilityDecisionType.DENY, reason, policyId);
    }

    public static CapabilityDecision requireApproval(String reason) {
        return new CapabilityDecision(CapabilityDecisionType.REQUIRE_APPROVAL, reason, null);
    }

    public static CapabilityDecision requireApproval(String reason, String policyId) {
        return new CapabilityDecision(CapabilityDecisionType.REQUIRE_APPROVAL, reason, policyId);
    }

    public boolean isAllowed() {
        return type == CapabilityDecisionType.ALLOW;
    }
}
