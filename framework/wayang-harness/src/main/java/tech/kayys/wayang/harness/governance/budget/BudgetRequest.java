package tech.kayys.wayang.harness.governance.budget;

import java.util.Objects;

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
