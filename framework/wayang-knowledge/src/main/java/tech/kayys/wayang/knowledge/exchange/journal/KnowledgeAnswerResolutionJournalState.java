package tech.kayys.wayang.knowledge.exchange.journal;

/**
 * Represents a knowledge answer resolution journal state.
 *
 * <p>Its components capture `last index`, `committed index`, `applied index`, `current term`, `current epoch id`.</p>
 *
 * @param lastIndex the last index
 * @param committedIndex the committed index
 * @param appliedIndex the applied index
 * @param currentTerm the current term
 * @param currentEpochId the current epoch id
 */


public record KnowledgeAnswerResolutionJournalState(
        long lastIndex,
        long committedIndex,
        long appliedIndex,
        long currentTerm,
        String currentEpochId
) {
    public KnowledgeAnswerResolutionJournalState {
        if (lastIndex < -1 || committedIndex < -1 || appliedIndex < -1 || currentTerm < 0) {
            throw new IllegalArgumentException("invalid journal state");
        }

        if (committedIndex > lastIndex && lastIndex != -1) {
            throw new IllegalArgumentException("commit index exceeds last index");
        }

        if (appliedIndex > committedIndex && committedIndex != -1) {
            throw new IllegalArgumentException("applied index exceeds commit index");
        }
    }
}
