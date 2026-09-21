package tech.kayys.andalus.knowledge.replay;

import tech.kayys.andalus.knowledge.decision.KnowledgeDecisionTrace;

/**
 * Defines the contract for knowledge replay context provider operations in the Andalus framework.
 */


public interface KnowledgeReplayContextProvider {

    KnowledgeReplayContext context(
            KnowledgeDecisionTrace trace,
            KnowledgeReplayRequest request);
}
