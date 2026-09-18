package tech.kayys.wayang.harness.governance.budget;

import java.util.Objects;

/**
 * Represents a budget request.
 *
 * <p>Its components capture `dimension`, `estimated cost`, `operation`.</p>
 *
 * @param dimension the dimension
 * @param estimatedCost the estimated cost
 * @param operation the operation
 */


public record BudgetRequest(
        BudgetDimension dimension,
        BudgetAmount estimatedCost,
        String operation
) {
    public BudgetRequest {
        Objects.requireNonNull(dimension, "dimension");
        Objects.requireNonNull(estimatedCost, "estimatedCost");
        operation = operation == null ? "default" : operation;
    }

    public static BudgetRequest of(BudgetDimension dimension, BudgetAmount estimatedCost) {
        return new BudgetRequest(dimension, estimatedCost, "default");
    }
}
