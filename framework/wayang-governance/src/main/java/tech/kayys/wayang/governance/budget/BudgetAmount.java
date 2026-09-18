package tech.kayys.wayang.governance.budget;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Exact numeric value and unit for budget allocation and tracking.
 */
public record BudgetAmount(
        BigDecimal value,
        String unit
) {
    public BudgetAmount {
        Objects.requireNonNull(value, "value");
        unit = unit == null ? "units" : unit;
        if (value.signum() < 0) {
            throw new IllegalArgumentException("Budget amount cannot be negative: " + value);
        }
    }

    public static BudgetAmount of(long value, String unit) {
        return new BudgetAmount(BigDecimal.valueOf(value), unit);
    }

    public static BudgetAmount of(double value, String unit) {
        return new BudgetAmount(BigDecimal.valueOf(value), unit);
    }

    public static BudgetAmount zero(String unit) {
        return new BudgetAmount(BigDecimal.ZERO, unit);
    }

    public BudgetAmount add(BudgetAmount other) {
        Objects.requireNonNull(other, "other");
        return new BudgetAmount(this.value.add(other.value), this.unit);
    }

    public BudgetAmount subtract(BudgetAmount other) {
        Objects.requireNonNull(other, "other");
        BigDecimal result = this.value.subtract(other.value);
        if (result.signum() < 0) {
            result = BigDecimal.ZERO;
        }
        return new BudgetAmount(result, this.unit);
    }

    public boolean isGreaterThan(BudgetAmount other) {
        return this.value.compareTo(other.value) > 0;
    }
}
