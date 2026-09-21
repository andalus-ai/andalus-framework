package tech.kayys.andalus.governance.budget;

/**
 * Defines the contract for budget reservation operations in the Andalus framework.
 */


public interface BudgetReservation {

    String id();

    BudgetRequest request();

    BudgetAmount reserved();

    boolean active();
}
