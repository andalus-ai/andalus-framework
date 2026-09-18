package tech.kayys.wayang.knowledge.replay;

/**
 * Defines the contract for knowledge replay engine operations in the Wayang framework.
 */


public interface KnowledgeReplayEngine {

    KnowledgeReplayResult replay(KnowledgeReplayRequest request);
}
