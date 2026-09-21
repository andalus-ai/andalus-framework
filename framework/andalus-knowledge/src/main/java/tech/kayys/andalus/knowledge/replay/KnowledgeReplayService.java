package tech.kayys.andalus.knowledge.replay;

/**
 * Defines the contract for knowledge replay service operations in the Andalus framework.
 */


public interface KnowledgeReplayService {

    KnowledgeReplayResult replay(KnowledgeReplayRequest request);
}
