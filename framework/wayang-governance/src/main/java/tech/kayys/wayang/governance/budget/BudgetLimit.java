package tech.kayys.wayang.governance.budget;

import java.util.Objects;

/**
 * Represents a budget limit.
 *
 * <p>Its components capture `dimension`, `maximum`.</p>
 *
 * @param dimension the dimension
 * @param maximum the maximum
 */


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
