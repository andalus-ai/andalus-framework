package tech.kayys.wayang.harness.memory;

import java.util.List;
import java.util.Map;

/**
 * Represents a memory query result.
 *
 * <p>Its components capture `entries`, `metadata`.</p>
 *
 * @param entries the entries
 * @param metadata the metadata
 */


public record MemoryQueryResult(
        List<MemoryEntry> entries,
        Map<String, Object> metadata
) {
    public MemoryQueryResult {
        entries = entries == null ? List.of() : List.copyOf(entries);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static MemoryQueryResult of(List<MemoryEntry> entries) {
        return new MemoryQueryResult(entries, Map.of());
    }
}
