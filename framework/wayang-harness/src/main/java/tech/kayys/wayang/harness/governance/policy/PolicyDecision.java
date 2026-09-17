package tech.kayys.wayang.harness.governance.policy;

/**
 * Outcome of evaluating an action against governance policies.
 */
public sealed interface PolicyDecision permits AllowDecision, DenyDecision, ApprovalDecision {

    String policyId();

    String reason();

    default boolean isAllowed() {
        return this instanceof AllowDecision;
    }

    default boolean isDenied() {
        return this instanceof DenyDecision;
    }

    default boolean requiresApproval() {
        return this instanceof ApprovalDecision;
    }
}
