package tech.kayys.andalus.governance.budget;

/**
 * Strategy SPI recommending cost and provider options for agent planning.
 */
public interface BudgetStrategy {

    BudgetPlan plan(BudgetContext context, BudgetRequest request);
}
