package tech.kayys.wayang.harness.governance.budget;

/**
 * Defines the contract for budget reservation operations in the Wayang framework.
 */


public interface BudgetReservation {

    String id();

    BudgetRequest request();

    BudgetAmount reserved();

    boolean active();
}
