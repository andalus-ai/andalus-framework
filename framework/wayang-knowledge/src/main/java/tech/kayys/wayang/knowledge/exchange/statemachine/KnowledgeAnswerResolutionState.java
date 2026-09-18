package tech.kayys.wayang.knowledge.exchange.statemachine;

import java.util.Map;
import java.util.Set;

/**
 * Represents a knowledge answer resolution state.
 *
 * <p>Its components capture `last applied index`, `current term`, `current epoch id`, `consensuses`, `revoked consensus ids`, and other values.</p>
 *
 * @param lastAppliedIndex the last applied index
 * @param currentTerm the current term
 * @param currentEpochId the current epoch id
 * @param consensuses the consensuses
 * @param revokedConsensusIds the revoked consensus ids
 * @param activeLeaseIds the active lease ids
 * @param activeRuntimeIds the active runtime ids
 * @param entries the entries
 */


public record KnowledgeAnswerResolutionState(
        long lastAppliedIndex,
        long currentTerm,
        String currentEpochId,
        Map<String, KnowledgeAnswerResolutionConsensusState> consensuses,
        Set<String> revokedConsensusIds,
        Set<String> activeLeaseIds,
        Set<String> activeRuntimeIds,
        Map<String, byte[]> entries
) {
    public KnowledgeAnswerResolutionState(
            long lastAppliedIndex,
            long currentTerm,
            String currentEpochId,
            Map<String, KnowledgeAnswerResolutionConsensusState> consensuses,
            Set<String> revokedConsensusIds,
            Set<String> activeLeaseIds,
            Set<String> activeRuntimeIds) {
        this(lastAppliedIndex, currentTerm, currentEpochId, consensuses,
                revokedConsensusIds, activeLeaseIds, activeRuntimeIds, Map.of());
    }

    public KnowledgeAnswerResolutionState {
        consensuses = consensuses == null ? Map.of() : Map.copyOf(consensuses);
        revokedConsensusIds = revokedConsensusIds == null ? Set.of() : Set.copyOf(revokedConsensusIds);
        activeLeaseIds = activeLeaseIds == null ? Set.of() : Set.copyOf(activeLeaseIds);
        activeRuntimeIds = activeRuntimeIds == null ? Set.of() : Set.copyOf(activeRuntimeIds);
        entries = entries == null ? Map.of() : Map.copyOf(entries);
    }

    public static KnowledgeAnswerResolutionState initial() {
        return new KnowledgeAnswerResolutionState(-1, 0, null, Map.of(), Set.of(), Set.of(), Set.of(), Map.of());
    }
}
