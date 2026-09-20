package tech.kayys.wayang.execution.core.economics;

import tech.kayys.wayang.execution.economics.ResourceBalance;
import tech.kayys.wayang.execution.economics.ResourceLedger;
import tech.kayys.wayang.execution.economics.ResourceUsage;
import tech.kayys.wayang.execution.mobility.ExecutionIdentity;
import tech.kayys.wayang.execution.resource.model.ResourceScope;
import tech.kayys.wayang.execution.resource.model.ResourceType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * In-memory thread-safe implementation of ResourceLedger tracking debits and credits.
 */
public class DefaultResourceLedger implements ResourceLedger {

    private final Map<String, List<ResourceUsage>> executionHistory = new ConcurrentHashMap<>();
    private final Map<String, Double> scopeAggregates = new ConcurrentHashMap<>();

    @Override
    public void recordUsage(ResourceUsage usage) {
        Objects.requireNonNull(usage, "usage cannot be null");
        executionHistory.computeIfAbsent(usage.executionId().id(), k -> new CopyOnWriteArrayList<>()).add(usage);

        String scopeKey = usage.type().name();
        scopeAggregates.merge(scopeKey, usage.unitsConsumed(), Double::sum);
    }

    @Override
    public ResourceBalance balance(ResourceScope scope, ResourceType type) {
        Objects.requireNonNull(scope, "scope cannot be null");
        Objects.requireNonNull(type, "type cannot be null");

        double consumed = scopeAggregates.getOrDefault(type.name(), 0.0);
        double quota = 1000.0; // Default capacity limit
        double budget = 5000.0; // Default budget limit
        return new ResourceBalance(scope, type, consumed, Math.max(0.0, quota - consumed), Math.max(0.0, budget - consumed));
    }

    @Override
    public List<ResourceUsage> history(ExecutionIdentity executionId) {
        if (executionId == null) return List.of();
        List<ResourceUsage> list = executionHistory.get(executionId.id());
        return list != null ? List.copyOf(list) : List.of();
    }
}
