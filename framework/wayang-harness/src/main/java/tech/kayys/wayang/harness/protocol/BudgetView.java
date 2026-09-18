package tech.kayys.wayang.harness.protocol;

import tech.kayys.wayang.harness.governance.budget.BudgetAmount;
import tech.kayys.wayang.harness.governance.budget.BudgetDimension;

import java.util.Map;
import java.util.Optional;

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
