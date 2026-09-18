package tech.kayys.wayang.execution.governance.audit;

/**
 * Categorization of security-relevant governance state transitions.
 */
public enum SecurityEventType {

    TOOL_INVOCATION_STARTED,

    TOOL_INVOCATION_ALLOWED,

    TOOL_INVOCATION_DENIED,

    TOOL_INVOCATION_FAILED,

    TOOL_APPROVAL_REQUIRED,

    TOOL_APPROVAL_APPROVED,

    TOOL_APPROVAL_REJECTED,

    TOOL_APPROVAL_EXPIRED,

    TOOL_APPROVAL_CANCELLED,

    POLICY_EVALUATED,

    AUTHORIZATION_DENIED
}
