package tech.kayys.wayang.governance.policy;

import tech.kayys.wayang.governance.approval.ApprovalRequest;

/**
 * Represents a approval decision.
 *
 * <p>Its components capture `policy id`, `reason`, `request`.</p>
 *
 * @param policyId the policy id
 * @param reason the reason
 * @param request the request
 */


public record ApprovalDecision(
        String policyId,
        String reason,
        ApprovalRequest request
) implements PolicyDecision {
    public ApprovalDecision {
        policyId = policyId == null ? "default" : policyId;
        reason = reason == null ? "Approval required by policy" : reason;
    }

    public static ApprovalDecision of(String policyId, String reason, ApprovalRequest request) {
        return new ApprovalDecision(policyId, reason, request);
    }
}
