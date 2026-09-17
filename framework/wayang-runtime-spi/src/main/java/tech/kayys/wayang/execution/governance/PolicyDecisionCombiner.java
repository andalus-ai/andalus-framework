package tech.kayys.wayang.execution.governance;

import java.util.Objects;

/**
 * Combines two {@link PolicyDecision} instances according to the monotonic security algebra:
 * {@code DENY > REQUIRE_APPROVAL > ALLOW}.
 */
public final class PolicyDecisionCombiner {

    private PolicyDecisionCombiner() {
    }

    public static PolicyDecision combine(PolicyDecision current, PolicyDecision candidate) {
        Objects.requireNonNull(current, "current decision cannot be null");
        Objects.requireNonNull(candidate, "candidate decision cannot be null");

        if (current instanceof PolicyDecision.Deny || current.isDenied()) {
            return current;
        }

        if (candidate instanceof PolicyDecision.Deny || candidate.isDenied()) {
            return candidate;
        }

        if (current instanceof PolicyDecision.RequireApproval || current.requiresApproval()) {
            return current;
        }

        if (candidate instanceof PolicyDecision.RequireApproval || candidate.requiresApproval()) {
            return candidate;
        }

        return PolicyDecision.allow();
    }
}
