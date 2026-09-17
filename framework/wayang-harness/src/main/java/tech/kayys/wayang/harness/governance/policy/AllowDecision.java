package tech.kayys.wayang.harness.governance.policy;

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
