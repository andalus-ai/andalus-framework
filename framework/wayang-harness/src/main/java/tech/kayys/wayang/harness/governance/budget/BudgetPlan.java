package tech.kayys.wayang.harness.governance.budget;

import java.util.List;

public record BudgetPlan(
        List<BudgetOption> options
) {
    public BudgetPlan {
        options = options == null ? List.of() : List.copyOf(options);
    }

    public static BudgetPlan of(BudgetOption... options) {
        return new BudgetPlan(List.of(options));
    }
}
