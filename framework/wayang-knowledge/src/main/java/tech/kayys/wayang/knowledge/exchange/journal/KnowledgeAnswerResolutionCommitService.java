package tech.kayys.wayang.knowledge.exchange.journal;

/**
 * Defines the contract for knowledge answer resolution commit service operations in the Wayang framework.
 */


public interface KnowledgeAnswerResolutionCommitService {

    KnowledgeAnswerResolutionCommitIndex commitThrough(long index, long term);

    KnowledgeAnswerResolutionCommitIndex applyThrough(long index);

    KnowledgeAnswerResolutionCommitIndex currentIndex();
}
