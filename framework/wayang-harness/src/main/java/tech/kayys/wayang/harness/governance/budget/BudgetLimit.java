package tech.kayys.wayang.harness.governance.budget;

import java.util.Objects;

public record BudgetLimit(
        BudgetDimension dimension,
        BudgetAmount maximum
) {
    public BudgetLimit {
        Objects.requireNonNull(dimension, "dimension");
        Objects.requireNonNull(maximum, "maximum");
    }

    public static BudgetLimit of(BudgetDimension dimension, BudgetAmount maximum) {
        return new BudgetLimit(dimension, maximum);
    }
}
