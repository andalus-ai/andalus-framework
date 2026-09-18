package tech.kayys.wayang.harness.protocol;

import tech.kayys.wayang.governance.budget.BudgetAmount;
import tech.kayys.wayang.governance.budget.BudgetDimension;

import java.util.Map;
import java.util.Optional;

/**
 * Defines the contract for budget view operations in the Wayang framework.
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
