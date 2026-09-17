package tech.kayys.wayang.harness.governance.budget;

import java.util.Objects;

public record BudgetUsage(
        BudgetDimension dimension,
        BudgetAmount consumed,
        BudgetAmount reserved
) {
    public BudgetUsage {
        Objects.requireNonNull(dimension, "dimension");
        consumed = consumed == null ? BudgetAmount.zero(dimension.unit()) : consumed;
        reserved = reserved == null ? BudgetAmount.zero(dimension.unit()) : reserved;
    }

    public BudgetAmount totalAllocated() {
        return consumed.add(reserved);
    }
}
