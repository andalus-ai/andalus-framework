package tech.kayys.andalus.governance.budget;

/**
 * Session-aware budget ledger contract for reserving, committing, and releasing resource quotas.
 */
public interface SessionBudgetLedger {

    /**
     * Retrieves current budget snapshot for the given session.
     */
    BudgetSnapshot current(String sessionId);

    /**
     * Reserves resource capacity for a pending session execution attempt.
     */
    BudgetReservation reserve(String sessionId, ResourceCost cost);

    /**
     * Commits actual usage after execution attempt completes.
     */
    void commit(BudgetReservation reservation, ResourceCost actual);

    /**
     * Releases an unused reservation.
     */
    void release(BudgetReservation reservation);
}
