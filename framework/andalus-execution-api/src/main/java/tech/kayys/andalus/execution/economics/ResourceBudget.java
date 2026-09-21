package tech.kayys.andalus.execution.economics;

import tech.kayys.andalus.execution.resource.model.ResourceScope;

import java.util.Map;
import java.util.Objects;

/**
 * Budget constraints governing resource consumption across an execution scope.
 */
public record ResourceBudget(
    String budgetId,
    ResourceScope scope,
    Map<BudgetDimension, BudgetLimit> limits,
    BudgetPolicy policy
) {
    public ResourceBudget {
        Objects.requireNonNull(budgetId, "budgetId cannot be null");
        Objects.requireNonNull(scope, "scope cannot be null");
        limits = limits != null ? Map.copyOf(limits) : Map.of();
        Objects.requireNonNull(policy, "policy cannot be null");
    }

    public static ResourceBudget of(String budgetId, ResourceScope scope, Map<BudgetDimension, BudgetLimit> limits) {
        return new ResourceBudget(budgetId, scope, limits, BudgetPolicy.STRICT_HALT);
    }

    public boolean isExhausted() {
        return limits.values().stream().anyMatch(BudgetLimit::isExceeded);
    }
}
