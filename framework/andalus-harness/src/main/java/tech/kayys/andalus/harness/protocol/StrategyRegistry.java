package tech.kayys.andalus.harness.protocol;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for configuring and retrieving available {@link DecisionStrategy} instances by ID.
 */
public class StrategyRegistry {

    private final Map<String, DecisionStrategy> strategies = new ConcurrentHashMap<>();

    public void register(DecisionStrategy strategy) {
        Objects.requireNonNull(strategy, "strategy cannot be null");
        strategies.put(strategy.id(), strategy);
    }

    public Optional<DecisionStrategy> get(String strategyId) {
        if (strategyId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(strategies.get(strategyId));
    }

    public DecisionStrategy getOrDefault(String strategyId, DecisionStrategy defaultStrategy) {
        return get(strategyId).orElse(defaultStrategy);
    }

    public boolean has(String strategyId) {
        return strategyId != null && strategies.containsKey(strategyId);
    }
}
