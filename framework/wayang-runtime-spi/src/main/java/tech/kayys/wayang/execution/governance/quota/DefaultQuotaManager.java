package tech.kayys.wayang.execution.governance.quota;

import tech.kayys.wayang.execution.governance.PolicyEvaluationContext;
import tech.kayys.wayang.execution.governance.ToolBudget;
import tech.kayys.wayang.tool.ToolInvocation;

import java.util.Objects;

/**
 * Standard implementation of {@link QuotaManager} leveraging {@link RateLimiter} and {@link ToolBudgetLedger}.
 */
public final class DefaultQuotaManager implements QuotaManager {

    private final RateLimiter rateLimiter;
    private final ToolBudgetLedger budgetLedger;

    public DefaultQuotaManager(RateLimiter rateLimiter, ToolBudgetLedger budgetLedger) {
        this.rateLimiter = Objects.requireNonNull(rateLimiter, "rateLimiter cannot be null");
        this.budgetLedger = Objects.requireNonNull(budgetLedger, "budgetLedger cannot be null");
    }

    @Override
    public void acquirePermit(ToolInvocation invocation, PolicyEvaluationContext context) {
        String rateKey = context.tenantId() != null ? context.tenantId() : (context.userId() != null ? context.userId() : "default");

        if (!rateLimiter.tryAcquire(rateKey)) {
            throw new QuotaExceededException("Tool rate limit exceeded for key: " + rateKey, "RATE_LIMIT_EXCEEDED", rateKey);
        }

        ToolBudget budget = budgetLedger.budgetFor(context.tenantId(), context.userId());
        if (budget.isCallBudgetExhausted()) {
            throw new QuotaExceededException("Tool invocation call budget exhausted for tenant: " + context.tenantId(), "CALL_BUDGET_EXHAUSTED", rateKey);
        }
    }

    @Override
    public void recordConsumption(ToolInvocation invocation, PolicyEvaluationContext context, long durationMs, double costUsd) {
        ToolBudget budget = budgetLedger.budgetFor(context.tenantId(), context.userId());
        budget.consume(durationMs, costUsd);
    }
}
