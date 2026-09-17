package tech.kayys.wayang.harness.governance.budget;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultBudgetPolicy implements BudgetPolicy {

    private final Map<BudgetDimension, BudgetLimit> limits = new ConcurrentHashMap<>();
    private final BudgetLedger ledger;

    public DefaultBudgetPolicy(BudgetLedger ledger) {
        this(ledger, Map.of());
    }

    public DefaultBudgetPolicy(BudgetLedger ledger, Map<BudgetDimension, BudgetLimit> limits) {
        this.ledger = Objects.requireNonNull(ledger, "ledger");
        if (limits != null) {
            this.limits.putAll(limits);
        }
    }

    public void setLimit(BudgetLimit limit) {
        Objects.requireNonNull(limit, "limit");
        limits.put(limit.dimension(), limit);
    }

    @Override
    public BudgetDecision evaluate(BudgetContext context, BudgetRequest request) {
        Objects.requireNonNull(request, "request");
        BudgetLimit limit = limits.get(request.dimension());

        if (limit == null) {
            return BudgetDecision.allow("No budget limit configured for " + request.dimension().id(), null);
        }

        BudgetUsage currentUsage = ledger.usage(request.dimension());
        BigDecimal totalProjected = currentUsage.totalAllocated().value().add(request.estimatedCost().value());

        if (totalProjected.compareTo(limit.maximum().value()) > 0) {
            return BudgetDecision.deny("Budget limit exceeded for " + request.dimension().id() + ": limit=" + limit.maximum().value() + ", projected=" + totalProjected);
        }

        BigDecimal remaining = limit.maximum().value().subtract(totalProjected);
        BudgetAmount remainingAmount = new BudgetAmount(remaining, request.dimension().unit());

        // Check warn threshold (e.g. >= 80% used)
        BigDecimal eightyPercent = limit.maximum().value().multiply(new BigDecimal("0.8"));
        if (totalProjected.compareTo(eightyPercent) >= 0) {
            return BudgetDecision.warn("Budget warning: 80% or more allocated for " + request.dimension().id(), remainingAmount);
        }

        return BudgetDecision.allow("Budget allocated within limit", remainingAmount);
    }
}
