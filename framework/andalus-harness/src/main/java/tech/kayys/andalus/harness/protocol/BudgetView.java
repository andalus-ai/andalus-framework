package tech.kayys.andalus.harness.protocol;

import tech.kayys.andalus.governance.budget.BudgetAmount;
import tech.kayys.andalus.governance.budget.BudgetDimension;

import java.util.Map;
import java.util.Optional;

/**
 * Defines the contract for budget view operations in the Andalus framework.
 */


public interface BudgetView {

    Optional<BudgetAmount> remaining(BudgetDimension dimension);

    Map<BudgetDimension, BudgetAmount> snapshot();

    static BudgetView empty() {
        return new BudgetView() {
            @Override
            public Optional<BudgetAmount> remaining(BudgetDimension dimension) {
                return Optional.empty();
            }

            @Override
            public Map<BudgetDimension, BudgetAmount> snapshot() {
                return Map.of();
            }
        };
    }
}
