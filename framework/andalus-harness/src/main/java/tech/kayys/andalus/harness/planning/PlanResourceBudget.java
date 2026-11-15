package tech.kayys.andalus.harness.planning;

import java.math.BigDecimal;

/**
 * Optional upper bounds for a proposed plan.
 *
 * <p>A {@code null} bound means that dimension is unconstrained. Cost and
 * duration units are application-defined but must be consistent across all
 * step estimates in a plan. Duration is expressed in milliseconds.</p>
 */
public record PlanResourceBudget(
        Integer maxSteps,
        BigDecimal maxEstimatedCost,
        Long maxEstimatedDurationMillis,
        Long maxEstimatedTokens) {

    public PlanResourceBudget {
        if (maxSteps != null && maxSteps < 1) {
            throw new IllegalArgumentException("maxSteps must be positive when specified.");
        }
        if (maxEstimatedCost != null && maxEstimatedCost.signum() < 0) {
            throw new IllegalArgumentException("maxEstimatedCost must not be negative.");
        }
        if (maxEstimatedDurationMillis != null && maxEstimatedDurationMillis < 0) {
            throw new IllegalArgumentException("maxEstimatedDurationMillis must not be negative.");
        }
        if (maxEstimatedTokens != null && maxEstimatedTokens < 0) {
            throw new IllegalArgumentException("maxEstimatedTokens must not be negative.");
        }
    }

    public static PlanResourceBudget unrestricted() {
        return new PlanResourceBudget(null, null, null, null);
    }
}
