package tech.kayys.wayang.knowledge.exchange.journal;

import java.util.List;

/**
 * Defines the contract for knowledge answer resolution journal recovery operations in the Wayang framework.
 */


public interface KnowledgeAnswerResolutionJournalRecovery {

    KnowledgeAnswerResolutionJournalState recover(
            KnowledgeAnswerResolutionJournal journal,
            long lastAppliedIndex
    );

    List<KnowledgeAnswerResolutionLogEntry> replayEntries(
            KnowledgeAnswerResolutionJournal journal,
            long fromIndex,
            long toIndex
    );
}
