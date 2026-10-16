package tech.kayys.andalus.harness.memory;

import java.util.HashMap;
import java.util.Map;

/**
 * Adapter converting between legacy MemoryRecord and canonical MemoryEntry.
 */
public final class MemoryRecordAdapter {

    private MemoryRecordAdapter() {}

    /**
     * Converts canonical MemoryEntry to a legacy MemoryRecord representation.
     */
    public static Map<String, Object> toRecordProperties(MemoryEntry entry) {
        if (entry == null) {
            return Map.of();
        }
        Map<String, Object> props = new HashMap<>();
        props.put("id", entry.id().asString());
        props.put("type", entry.type().name().toLowerCase());
        props.put("content", entry.content());
        props.put("scope", entry.scope().name());
        props.put("metadata", entry.metadata().attributes());
        props.put("confidence", entry.metadata().confidence());
        props.put("source", entry.metadata().source().agentId());
        return Map.copyOf(props);
    }

    /**
     * Converts legacy parameters into a canonical MemoryEntry.
     */
    public static MemoryEntry fromRecord(
            String id,
            String typeName,
            String content,
            String scopeName,
            double confidence,
            String sourceId,
            Map<String, Object> attributes
    ) {
        MemoryId memId = id != null && !id.isBlank() ? MemoryId.of(id) : MemoryId.generate();
        MemoryType type = MemoryType.FACT;
        if (typeName != null) {
            try {
                type = MemoryType.valueOf(typeName.trim().toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }
        MemoryScope scope = MemoryScope.AGENT;
        if (scopeName != null) {
            try {
                scope = MemoryScope.valueOf(scopeName.trim().toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }

        MemoryMetadata meta = MemoryMetadata.observed(sourceId != null ? sourceId : "adapter", "legacy", confidence)
                .withAttributes(attributes != null ? attributes : Map.of());

        return new MemoryEntry(memId, type, content != null ? content : "", scope, meta);
    }
}
