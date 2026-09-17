package tech.kayys.wayang.execution.governance;

/**
 * Validates semantic invariants on {@link PolicyDecision}.
 */
public final class PolicyDecisionValidator {

    private PolicyDecisionValidator() {
    }

    public static void validate(PolicyDecision decision) {
        if (decision == null) {
            throw new IllegalArgumentException("Policy decision cannot be null");
        }

        switch (decision.effect()) {
            case ALLOW -> validateAllow(decision);
            case DENY -> validateDeny(decision);
            case REQUIRE_APPROVAL -> validateApproval(decision);
        }
    }

    private static void validateAllow(PolicyDecision decision) {
        if (decision.approvalId() != null) {
            throw new IllegalArgumentException("ALLOW decision cannot contain approvalId");
        }
    }

    private static void validateDeny(PolicyDecision decision) {
        if (decision.approvalId() != null) {
            throw new IllegalArgumentException("DENY decision cannot contain approvalId");
        }
    }

    private static void validateApproval(PolicyDecision decision) {
        if (decision.decisionReason() != PolicyDecisionReason.APPROVAL_REQUIRED) {
            throw new IllegalArgumentException("REQUIRE_APPROVAL must use APPROVAL_REQUIRED reason");
        }
    }
}
