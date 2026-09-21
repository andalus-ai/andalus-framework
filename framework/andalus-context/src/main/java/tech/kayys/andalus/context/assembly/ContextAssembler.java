package tech.kayys.andalus.context.assembly;

import java.util.List;
import tech.kayys.andalus.context.compaction.ContextSlice;

/**
 * Contract for controlled context assembly and token-governed envelope production.
 */
public interface ContextAssembler {

    /**
     * Assembles a governed context envelope from available slices within requested token budget.
     */
    ContextEnvelope assemble(ContextRequest request, List<ContextSlice> candidateSlices);
}
