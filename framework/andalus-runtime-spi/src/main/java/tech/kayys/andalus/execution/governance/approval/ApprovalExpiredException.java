package tech.kayys.andalus.execution.governance.approval;

public final class ApprovalExpiredException extends RuntimeException {

    public ApprovalExpiredException(String approvalId) {
        super("Approval request has expired: " + approvalId);
    }
}
