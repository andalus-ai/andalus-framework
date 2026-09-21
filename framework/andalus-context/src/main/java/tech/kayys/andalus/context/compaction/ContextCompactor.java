package tech.kayys.andalus.context.compaction;

/**
 * Strategy interface for compacting context window slices when nearing token capacity.
 */
public interface ContextCompactor {

    /**
     * Compacts the given slice by summarizing older turns and keeping active reasoning.
     *
     * @param slice The input context slice
     * @param targetTokenBudget The target budget to reduce within
     * @return The compacted context slice
     */
    ContextSlice compact(ContextSlice slice, long targetTokenBudget);
}
