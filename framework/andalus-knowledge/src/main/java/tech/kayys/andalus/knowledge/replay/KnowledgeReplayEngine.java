package tech.kayys.andalus.knowledge.replay;

/**
 * Defines the contract for knowledge replay engine operations in the Andalus framework.
 */


public interface KnowledgeReplayEngine {

    KnowledgeReplayResult replay(KnowledgeReplayRequest request);
}
