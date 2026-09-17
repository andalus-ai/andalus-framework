package tech.kayys.wayang.harness.governance.budget;

public interface BudgetManager {

    BudgetReservation reserve(BudgetRequest request);

    void settle(BudgetReservation reservation, BudgetAmount actual);

    void release(BudgetReservation reservation);
}
