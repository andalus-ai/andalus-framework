package tech.kayys.andalus.execution.economics;

import java.util.Objects;

/**
 * Quantified ceiling for a single budget dimension.
 */
public record BudgetLimit(
    BudgetDimension dimension,
    double limitAmount,
    double currentSpent
) {
    public BudgetLimit {
        Objects.requireNonNull(dimension, "dimension cannot be null");
    }

    public static BudgetLimit of(BudgetDimension dimension, double limitAmount) {
        return new BudgetLimit(dimension, limitAmount, 0.0);
    }

    public double remaining() {
        return Math.max(0.0, limitAmount - currentSpent);
    }

    public boolean isExceeded() {
        return currentSpent >= limitAmount;
    }

    public BudgetLimit withSpent(double spent) {
        return new BudgetLimit(dimension, limitAmount, spent);
    }
}
