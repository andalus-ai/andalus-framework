package tech.kayys.andalus.knowledge;

public interface KnowledgeBudgetProvider {
    KnowledgeBudget budgetFor(KnowledgeRuntimeContext context);
    static KnowledgeBudgetProvider defaults() { return context -> KnowledgeBudget.defaults(); }
    static KnowledgeBudgetProvider fixed(KnowledgeBudget budget) { return context -> budget; }
}
