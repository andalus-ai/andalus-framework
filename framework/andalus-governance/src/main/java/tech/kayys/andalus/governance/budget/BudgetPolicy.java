package tech.kayys.andalus.governance.budget;

/**
 * Defines the contract for budget policy operations in the Andalus framework.
 */


public interface BudgetPolicy {

    BudgetDecision evaluate(BudgetContext context, BudgetRequest request);
}
