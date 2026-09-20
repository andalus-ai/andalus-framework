package tech.kayys.wayang.execution.adaptation;

import tech.kayys.wayang.execution.mobility.ExecutionIdentity;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Concrete adaptation plan containing proposed deltas and risk assessment.
 */
public record AdaptationPlan(
    String planId,
    ExecutionIdentity executionId,
    AdaptationType type,
    List<ExecutionDelta> deltas,
    RiskAssessment risk,
    Instant plannedAt
) {
    public AdaptationPlan {
        Objects.requireNonNull(planId, "planId cannot be null");
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(type, "type cannot be null");
        deltas = deltas != null ? List.copyOf(deltas) : List.of();
        Objects.requireNonNull(risk, "risk cannot be null");
        Objects.requireNonNull(plannedAt, "plannedAt cannot be null");
    }

    public static AdaptationPlan of(String planId, ExecutionIdentity executionId, AdaptationType type, List<ExecutionDelta> deltas, RiskAssessment risk) {
        return new AdaptationPlan(planId, executionId, type, deltas, risk, Instant.now());
    }
}
