package tech.kayys.wayang.execution.governance.approval;

public final class ToolApprovalRequiredException extends RuntimeException {

    private final String approvalId;

    public ToolApprovalRequiredException(String approvalId) {
        super("Tool execution requires approval: " + approvalId);
        this.approvalId = approvalId;
    }

    public String approvalId() {
        return approvalId;
    }
}
