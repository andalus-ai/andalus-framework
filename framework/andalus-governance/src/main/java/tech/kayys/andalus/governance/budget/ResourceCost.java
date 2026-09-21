package tech.kayys.andalus.governance.budget;

import java.util.Map;
import java.util.Objects;

/**
 * Quantifies resource cost across multiple dimensions (e.g. tokens, compute, storage, financial cost).
 */
public record ResourceCost(
        Map<BudgetDimension, Double> amounts) {

    public ResourceCost {
        amounts = amounts != null ? Map.copyOf(amounts) : Map.of();
    }

    public static ResourceCost of(BudgetDimension dimension, double amount) {
        return new ResourceCost(Map.of(dimension, amount));
    }

    public static ResourceCost of(Map<BudgetDimension, Double> amounts) {
        return new ResourceCost(amounts);
    }
}
