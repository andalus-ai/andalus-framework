package tech.kayys.andalus.context.assembly;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import tech.kayys.andalus.context.compaction.ContextSlice;

/**
 * Default budget-aware context assembler.
 */
public class DefaultContextAssembler implements ContextAssembler {

    @Override
    public ContextEnvelope assemble(ContextRequest request, List<ContextSlice> candidateSlices) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }
        if (candidateSlices == null || candidateSlices.isEmpty()) {
            return new ContextEnvelope(
                    request.sessionId(),
                    List.of(),
                    request.budget(),
                    0L,
                    Instant.now());
        }

        long budgetTokens = request.budget().inputTokens() + request.budget().retrievalTokens();
        List<ContextSlice> selected = new ArrayList<>();
        long currentTokens = 0;

        for (ContextSlice slice : candidateSlices) {
            long tokens = slice.estimatedTokens();
            if (currentTokens + tokens <= budgetTokens) {
                selected.add(slice);
                currentTokens += tokens;
            }
        }

        return new ContextEnvelope(
                request.sessionId(),
                selected,
                request.budget(),
                currentTokens,
                Instant.now());
    }
}
