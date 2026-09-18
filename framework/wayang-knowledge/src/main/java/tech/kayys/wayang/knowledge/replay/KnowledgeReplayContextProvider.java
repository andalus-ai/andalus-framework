package tech.kayys.wayang.knowledge.replay;

import tech.kayys.wayang.knowledge.decision.KnowledgeDecisionTrace;

/**
 * Defines the contract for knowledge replay context provider operations in the Wayang framework.
 */


public interface KnowledgeReplayContextProvider {

    KnowledgeReplayContext context(
            KnowledgeDecisionTrace trace,
            KnowledgeReplayRequest request);
}
