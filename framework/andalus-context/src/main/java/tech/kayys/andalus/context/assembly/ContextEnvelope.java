package tech.kayys.andalus.context.assembly;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import tech.kayys.andalus.context.compaction.ContextSlice;

/**
 * Governed context envelope returned to reasoning engines.
 * Supplies structured context fragments and explicit token allocation.
 */
public record ContextEnvelope(
        String sessionId,
        List<ContextSlice> slices,
        ContextBudget budget,
        long estimatedTokens,
        Instant assembledAt) {

    public ContextEnvelope {
        Objects.requireNonNull(sessionId, "sessionId must not be null");
        slices = slices != null ? List.copyOf(slices) : List.of();
        budget = budget != null ? budget : ContextBudget.standard();
        assembledAt = assembledAt != null ? assembledAt : Instant.now();
    }
}
