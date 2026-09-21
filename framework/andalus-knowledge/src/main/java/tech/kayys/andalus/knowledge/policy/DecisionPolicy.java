package tech.kayys.andalus.knowledge.policy;

import tech.kayys.andalus.knowledge.KnowledgeContext;
import tech.kayys.andalus.knowledge.reasoning.DecisionRequest;

/**
 * Domain-neutral policy governing a decision or recommendation.
 */
public interface DecisionPolicy {

    String id();

    default String description() {
        return id();
    }

    default int priority() {
        return 100;
    }

    PolicyDecision evaluate(
            DecisionRequest request,
            KnowledgeContext knowledgeContext
    );
}
