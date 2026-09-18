package tech.kayys.wayang.harness.governance.budget;

/**
 * Defines the contract for budget policy operations in the Wayang framework.
 */


public interface BudgetPolicy {

    BudgetDecision evaluate(BudgetContext context, BudgetRequest request);
}
