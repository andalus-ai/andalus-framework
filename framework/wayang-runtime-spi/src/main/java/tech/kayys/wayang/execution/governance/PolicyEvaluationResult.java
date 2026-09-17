package tech.kayys.wayang.execution.governance;

import java.util.List;
import java.util.Objects;

/**
 * Detailed result of multi-policy composition, containing the aggregate decision and individual evaluations.
 */
public record PolicyEvaluationResult(
        PolicyDecision decision,
        List<PolicyEvaluation> evaluations
) {

    public PolicyEvaluationResult {
        Objects.requireNonNull(decision, "decision cannot be null");
        evaluations = evaluations == null ? List.of() : List.copyOf(evaluations);
    }

    public boolean isAllowed() {
        return decision.isAllowed();
    }

    public boolean isDenied() {
        return decision.isDenied();
    }

    public boolean requiresApproval() {
        return decision.requiresApproval();
    }
}
