package tech.kayys.wayang.governance.budget;

import java.util.Objects;

/**
 * Default implementation of {@link HarnessBudget} integrating ledger, policy, and manager.
 */
public class DefaultHarnessBudget implements HarnessBudget {

    private final DefaultBudgetLedger ledger;
    private final BudgetPolicy policy;
    private final BudgetManager manager;

    public DefaultHarnessBudget() {
        this(new DefaultBudgetLedger());
    }

    public DefaultHarnessBudget(DefaultBudgetLedger ledger) {
        this(ledger, new DefaultBudgetPolicy(ledger));
    }

    public DefaultHarnessBudget(DefaultBudgetLedger ledger, BudgetPolicy policy) {
        this(ledger, policy, new DefaultBudgetManager(ledger, policy));
    }

    public DefaultHarnessBudget(DefaultBudgetLedger ledger, BudgetPolicy policy, BudgetManager manager) {
        this.ledger = Objects.requireNonNull(ledger, "ledger");
        this.policy = Objects.requireNonNull(policy, "policy");
        this.manager = Objects.requireNonNull(manager, "manager");
    }

    @Override
    public BudgetSnapshot snapshot() {
        return ledger.snapshot();
    }

    @Override
    public BudgetDecision evaluate(BudgetRequest request) {
        return policy.evaluate(BudgetContext.of("harness-budget"), request);
    }

    @Override
    public BudgetReservation reserve(BudgetRequest request) {
        return manager.reserve(request);
    }

    @Override
    public void settle(BudgetReservation reservation, BudgetAmount actual) {
        manager.settle(reservation, actual);
    }

    @Override
    public void release(BudgetReservation reservation) {
        manager.release(reservation);
    }

    public DefaultBudgetLedger ledger() {
        return ledger;
    }
}
