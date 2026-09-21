package tech.kayys.andalus.memory.session;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Concrete payload stored within a memory tier.
 */
public record MemoryEntry(
        String entryId,
        MemoryTier tier,
        MemoryKind kind,
        String content,
        Map<String, Object> metadata,
        Instant timestamp) {

    public MemoryEntry {
        Objects.requireNonNull(entryId, "entryId must not be null");
        Objects.requireNonNull(tier, "tier must not be null");
        kind = kind != null ? kind : MemoryKind.FACT;
        content = content != null ? content : "";
        metadata = metadata != null ? Map.copyOf(metadata) : Map.of();
        timestamp = timestamp != null ? timestamp : Instant.now();
    }

    public static MemoryEntry of(String entryId, MemoryTier tier, String content, Map<String, Object> metadata) {
        return new MemoryEntry(entryId, tier, MemoryKind.FACT, content, metadata, Instant.now());
    }

    public static MemoryEntry of(String entryId, MemoryTier tier, MemoryKind kind, String content, Map<String, Object> metadata) {
        return new MemoryEntry(entryId, tier, kind, content, metadata, Instant.now());
    }
}
