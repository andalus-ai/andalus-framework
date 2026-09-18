package tech.kayys.wayang.harness.governance.budget;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Provides the default implementation of the budget reservation contract.
 */


public class DefaultBudgetReservation implements BudgetReservation {

    private final String id;
    private final BudgetRequest request;
    private final BudgetAmount reserved;
    private final AtomicBoolean active = new AtomicBoolean(true);

    public DefaultBudgetReservation(BudgetRequest request, BudgetAmount reserved) {
        this("resv-" + UUID.randomUUID(), request, reserved);
    }

    public DefaultBudgetReservation(String id, BudgetRequest request, BudgetAmount reserved) {
        this.id = Objects.requireNonNull(id, "id");
        this.request = Objects.requireNonNull(request, "request");
        this.reserved = Objects.requireNonNull(reserved, "reserved");
    }

    @Override public String id() { return id; }
    @Override public BudgetRequest request() { return request; }
    @Override public BudgetAmount reserved() { return reserved; }
    @Override public boolean active() { return active.get(); }

    public void deactivate() {
        active.set(false);
    }
}
