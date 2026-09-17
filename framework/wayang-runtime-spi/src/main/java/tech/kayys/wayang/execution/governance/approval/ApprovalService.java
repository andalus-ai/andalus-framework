package tech.kayys.wayang.execution.governance.approval;

/**
 * Service managing the lifecycle of approval requests.
 */
public interface ApprovalService {

    ApprovalRequest request(ApprovalRequestTemplate template);

    ApprovalRequest get(String approvalId);

    ApprovalRequest approve(String approvalId, String approverId, String reason);

    ApprovalRequest reject(String approvalId, String approverId, String reason);

    ApprovalRequest cancel(String approvalId, String reason);

    ApprovalRequest expire(String approvalId);

    boolean isApproved(String approvalId);
}
