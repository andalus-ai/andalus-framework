package tech.kayys.wayang.harness.governance.policy;

/**
 * Represents a deny decision.
 *
 * <p>Its components capture `policy id`, `reason`.</p>
 *
 * @param policyId the policy id
 * @param reason the reason
 */


public record DenyDecision(
        String policyId,
        String reason
) implements PolicyDecision {
    public DenyDecision {
        policyId = policyId == null ? "default" : policyId;
        reason = reason == null ? "Denied by policy" : reason;
    }

    public static DenyDecision of(String policyId, String reason) {
        return new DenyDecision(policyId, reason);
    }
}
