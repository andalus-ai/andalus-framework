package tech.kayys.wayang.execution.governance.approval;

public final class ApprovalNotFoundException extends RuntimeException {

    public ApprovalNotFoundException(String approvalId) {
        super("Approval request not found: " + approvalId);
    }
}
