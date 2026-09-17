package tech.kayys.wayang.execution.governance;

import java.util.Objects;

/**
 * Audit record of an individual policy's evaluation during composition.
 */
public record PolicyEvaluation(
        String policyId,
        int priority,
        PolicyDecision decision
) {

    public PolicyEvaluation {
        if (policyId == null || policyId.isBlank()) {
            throw new IllegalArgumentException("policyId cannot be null or blank");
        }
        policyId = policyId.trim();

        Objects.requireNonNull(decision, "decision cannot be null");
    }
}
