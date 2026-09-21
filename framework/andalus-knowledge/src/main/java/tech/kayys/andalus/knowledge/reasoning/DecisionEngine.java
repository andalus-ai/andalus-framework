package tech.kayys.andalus.knowledge.reasoning;

import tech.kayys.andalus.knowledge.KnowledgeContext;

import java.util.concurrent.CompletionStage;

/**
 * Generic decision and reasoning extension point.
 */
public interface DecisionEngine {

    CompletionStage<Decision> decide(
            DecisionRequest request,
            KnowledgeContext context
    );
}
