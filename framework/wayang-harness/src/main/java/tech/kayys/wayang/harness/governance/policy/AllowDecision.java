package tech.kayys.wayang.harness.governance.policy;

/**
 * Represents a allow decision.
 *
 * <p>Its components capture `policy id`, `reason`.</p>
 *
 * @param policyId the policy id
 * @param reason the reason
 */


public record AllowDecision(
        String policyId,
        String reason
) implements PolicyDecision {
    public AllowDecision {
        policyId = policyId == null ? "default" : policyId;
        reason = reason == null ? "Allowed by policy" : reason;
    }

    public static AllowDecision of(String policyId, String reason) {
        return new AllowDecision(policyId, reason);
    }
}
