package tech.kayys.andalus.memory.reactive;

import tech.kayys.andalus.memory.MemoryRecord;
import java.util.List;

/**
 * Decides which memory records should be consolidated/summarized.
 * Keeps the consolidation strategy decoupled from the store.
 */
public interface MemoryConsolidationPolicy {

    /** Returns records that should be consolidated from the candidate set */
    List<MemoryRecord> selectForConsolidation(List<MemoryRecord> candidates);

    /** No-op policy: consolidate nothing (useful for testing) */
    static MemoryConsolidationPolicy none() {
        return candidates -> List.of();
    }

    /** Consolidate all records older than a threshold turn count */
    static MemoryConsolidationPolicy afterNTurns(int n) {
        return candidates -> candidates.size() >= n ? candidates : List.of();
    }
}
