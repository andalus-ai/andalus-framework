package tech.kayys.wayang.governance.budget;

/**
 * Append-oriented financial ledger recording and aggregating budget consumption.
 */
public interface BudgetLedger {

    void record(BudgetConsumption consumption);

    BudgetUsage usage(BudgetDimension dimension);

    BudgetSnapshot snapshot();
}
