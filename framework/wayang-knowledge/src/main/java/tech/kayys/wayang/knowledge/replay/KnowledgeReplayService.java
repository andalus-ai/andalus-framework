package tech.kayys.wayang.knowledge.replay;

/**
 * Defines the contract for knowledge replay service operations in the Wayang framework.
 */


public interface KnowledgeReplayService {

    KnowledgeReplayResult replay(KnowledgeReplayRequest request);
}
