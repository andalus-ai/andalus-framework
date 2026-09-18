package tech.kayys.wayang.governance.budget;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides the default implementation of the budget manager contract.
 */


public class DefaultBudgetManager implements BudgetManager {

    private final DefaultBudgetLedger ledger;
    private final BudgetPolicy policy;
    private final Map<String, DefaultBudgetReservation> reservations = new ConcurrentHashMap<>();

    public DefaultBudgetManager(DefaultBudgetLedger ledger, BudgetPolicy policy) {
        this.ledger = Objects.requireNonNull(ledger, "ledger");
        this.policy = Objects.requireNonNull(policy, "policy");
    }

    @Override
    public BudgetReservation reserve(BudgetRequest request) {
        Objects.requireNonNull(request, "request");
        BudgetDecision decision = policy.evaluate(BudgetContext.of("exec-resv"), request);
        if (decision.isDenied()) {
            throw new IllegalStateException("Cannot reserve budget: " + decision.reason());
        }

        DefaultBudgetReservation resv = new DefaultBudgetReservation(request, request.estimatedCost());
        reservations.put(resv.id(), resv);
        ledger.addReservation(request.dimension(), request.estimatedCost());
        return resv;
    }

    @Override
    public void settle(BudgetReservation reservation, BudgetAmount actual) {
        Objects.requireNonNull(reservation, "reservation");
        Objects.requireNonNull(actual, "actual");

        DefaultBudgetReservation resv = reservations.remove(reservation.id());
        if (resv != null && resv.active()) {
            resv.deactivate();
            ledger.removeReservation(resv.request().dimension(), resv.reserved());
            ledger.record(new BudgetConsumption("exec-settled", resv.id(), resv.request().dimension(), actual, Instant.now(), "manager"));
        }
    }

    @Override
    public void release(BudgetReservation reservation) {
        if (reservation != null) {
            DefaultBudgetReservation resv = reservations.remove(reservation.id());
            if (resv != null && resv.active()) {
                resv.deactivate();
                ledger.removeReservation(resv.request().dimension(), resv.reserved());
            }
        }
    }
}
