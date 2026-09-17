package tech.kayys.wayang.execution.governance.audit;

/**
 * Types of tool governance and execution audit events.
 */
public enum ToolAuditEventType {
    POLICY_EVALUATED,
    EXECUTION_ALLOWED,
    EXECUTION_DENIED,
    APPROVAL_REQUESTED,
    APPROVAL_GRANTED,
    APPROVAL_REJECTED,
    EXECUTION_STARTED,
    EXECUTION_COMPLETED,
    EXECUTION_FAILED,
    BUDGET_EXCEEDED,
    RATE_LIMIT_EXCEEDED
}
