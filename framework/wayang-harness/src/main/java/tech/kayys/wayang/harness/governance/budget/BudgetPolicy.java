package tech.kayys.wayang.harness.governance.budget;

public interface BudgetPolicy {

    BudgetDecision evaluate(BudgetContext context, BudgetRequest request);
}
