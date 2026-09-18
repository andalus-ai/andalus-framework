package tech.kayys.wayang.governance.budget;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * In-memory thread-safe implementation of {@link BudgetLedger}.
 */
public class DefaultBudgetLedger implements BudgetLedger {

    private final List<BudgetConsumption> entries = new CopyOnWriteArrayList<>();
    private final Map<BudgetDimension, BigDecimal> consumedTotals = new ConcurrentHashMap<>();
    private final Map<BudgetDimension, BigDecimal> reservedTotals = new ConcurrentHashMap<>();
    private final Map<BudgetDimension, BudgetLimit> limits = new ConcurrentHashMap<>();

    public DefaultBudgetLedger() {}

    public DefaultBudgetLedger(Map<BudgetDimension, BudgetLimit> initialLimits) {
        if (initialLimits != null) {
            limits.putAll(initialLimits);
        }
    }

    public void setLimit(BudgetLimit limit) {
        Objects.requireNonNull(limit, "limit");
        limits.put(limit.dimension(), limit);
    }

    public void addReservation(BudgetDimension dimension, BudgetAmount amount) {
        reservedTotals.compute(dimension, (dim, current) ->
                (current == null ? BigDecimal.ZERO : current).add(amount.value()));
    }

    public void removeReservation(BudgetDimension dimension, BudgetAmount amount) {
        reservedTotals.compute(dimension, (dim, current) -> {
            if (current == null) return BigDecimal.ZERO;
            BigDecimal updated = current.subtract(amount.value());
            return updated.signum() < 0 ? BigDecimal.ZERO : updated;
        });
    }

    @Override
    public void record(BudgetConsumption consumption) {
        Objects.requireNonNull(consumption, "consumption");
        entries.add(consumption);
        consumedTotals.compute(consumption.dimension(), (dim, current) ->
                (current == null ? BigDecimal.ZERO : current).add(consumption.amount().value()));
    }

    @Override
    public BudgetUsage usage(BudgetDimension dimension) {
        Objects.requireNonNull(dimension, "dimension");
        BigDecimal consumed = consumedTotals.getOrDefault(dimension, BigDecimal.ZERO);
        BigDecimal reserved = reservedTotals.getOrDefault(dimension, BigDecimal.ZERO);
        return new BudgetUsage(dimension, new BudgetAmount(consumed, dimension.unit()), new BudgetAmount(reserved, dimension.unit()));
    }

    @Override
    public BudgetSnapshot snapshot() {
        Map<BudgetDimension, BudgetUsage> usages = new ConcurrentHashMap<>();
        for (BudgetDimension dim : limits.keySet()) {
            usages.put(dim, usage(dim));
        }
        for (BudgetDimension dim : consumedTotals.keySet()) {
            usages.putIfAbsent(dim, usage(dim));
        }
        for (BudgetDimension dim : reservedTotals.keySet()) {
            usages.putIfAbsent(dim, usage(dim));
        }
        return new BudgetSnapshot("snap-" + System.currentTimeMillis(), Instant.now(), usages, limits);
    }
}
