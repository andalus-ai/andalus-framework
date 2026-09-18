package tech.kayys.wayang.knowledge.exchange.checkpoint;

import tech.kayys.wayang.knowledge.exchange.journal.KnowledgeAnswerResolutionJournal;

/**
 * Defines the contract for knowledge answer resolution compactor operations in the Wayang framework.
 */


public interface KnowledgeAnswerResolutionCompactor {

    long compact(
            KnowledgeAnswerResolutionJournal journal,
            KnowledgeAnswerResolutionCheckpointStore checkpointStore,
            KnowledgeAnswerResolutionCheckpointPolicy policy
    );
}
