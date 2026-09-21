package tech.kayys.andalus.governance.budget;

/**
 * Defines the contract for budget manager operations in the Andalus framework.
 */


public interface BudgetManager {

    BudgetReservation reserve(BudgetRequest request);

    void settle(BudgetReservation reservation, BudgetAmount actual);

    void release(BudgetReservation reservation);
}
