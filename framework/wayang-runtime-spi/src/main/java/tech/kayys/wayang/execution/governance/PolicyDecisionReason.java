package tech.kayys.wayang.execution.governance;

/**
 * Standard machine-readable reasons for policy evaluation decisions.
 */
public enum PolicyDecisionReason {

    /**
     * No policy prevented execution.
     */
    POLICY_ALLOWED,

    /**
     * A policy explicitly denied execution.
     */
    POLICY_DENIED,

    /**
     * A policy requires explicit human or system approval.
     */
    APPROVAL_REQUIRED,

    /**
     * The requested permission was not granted.
     */
    PERMISSION_NOT_GRANTED,

    /**
     * The requested tool is not allowed.
     */
    TOOL_NOT_ALLOWED,

    /**
     * The caller's role is not authorized.
     */
    ROLE_NOT_AUTHORIZED,

    /**
     * The tenant is not authorized.
     */
    TENANT_NOT_AUTHORIZED,

    /**
     * The user is not authorized.
     */
    USER_NOT_AUTHORIZED,

    /**
     * The requested resource is outside the allowed scope.
     */
    RESOURCE_NOT_ALLOWED,

    /**
     * The policy configuration itself prevented evaluation.
     */
    POLICY_CONFIGURATION_ERROR,

    /**
     * A policy evaluation failed unexpectedly.
     */
    POLICY_EVALUATION_ERROR,

    /**
     * No applicable policy matched the invocation.
     */
    NO_MATCHING_POLICY
}
