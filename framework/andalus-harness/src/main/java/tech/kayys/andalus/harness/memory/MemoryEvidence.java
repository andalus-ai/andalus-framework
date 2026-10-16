package tech.kayys.andalus.harness.memory;

import java.util.Map;
import java.util.Objects;

/**
 * Result of memory retrieval paired with a relevance score and metadata.
 */
public record MemoryEvidence(
        MemoryEntry entry,
        double score,
        String retrievalMethod,
        Map<String, Object> metadata
) {

    public MemoryEvidence {
        Objects.requireNonNull(entry, "entry must not be null");
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static MemoryEvidence of(MemoryEntry entry, double score, String retrievalMethod) {
        return new MemoryEvidence(entry, score, retrievalMethod, Map.of());
    }
}
