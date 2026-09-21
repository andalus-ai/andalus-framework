package tech.kayys.andalus.execution.governance.approval;

public final class ApprovalNotFoundException extends RuntimeException {

    public ApprovalNotFoundException(String approvalId) {
        super("Approval request not found: " + approvalId);
    }
}
