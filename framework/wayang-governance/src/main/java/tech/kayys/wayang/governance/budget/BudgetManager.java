package tech.kayys.wayang.governance.budget;

/**
 * Defines the contract for budget manager operations in the Wayang framework.
 */


public interface BudgetManager {

    BudgetReservation reserve(BudgetRequest request);

    void settle(BudgetReservation reservation, BudgetAmount actual);

    void release(BudgetReservation reservation);
}
