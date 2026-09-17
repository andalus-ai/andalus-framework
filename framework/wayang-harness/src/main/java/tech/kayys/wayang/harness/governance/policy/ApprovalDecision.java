package tech.kayys.wayang.harness.governance.policy;

import tech.kayys.wayang.harness.governance.approval.ApprovalRequest;

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
