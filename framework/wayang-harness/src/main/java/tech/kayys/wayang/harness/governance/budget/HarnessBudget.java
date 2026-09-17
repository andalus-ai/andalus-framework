package tech.kayys.wayang.harness.governance.budget;

/**
 * Top-level Harness budget governance contract managing reservations, settlement, and ledgers.
 */
public interface HarnessBudget {

    BudgetSnapshot snapshot();

    BudgetDecision evaluate(BudgetRequest request);

    BudgetReservation reserve(BudgetRequest request);

    void settle(BudgetReservation reservation, BudgetAmount actual);

    void release(BudgetReservation reservation);
}
