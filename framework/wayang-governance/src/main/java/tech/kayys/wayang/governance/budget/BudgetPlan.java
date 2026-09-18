package tech.kayys.wayang.governance.budget;

import java.util.List;

/**
 * Represents a budget plan.
 *
 * <p>Its components capture `options`.</p>
 *
 * @param options the options
 */


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
