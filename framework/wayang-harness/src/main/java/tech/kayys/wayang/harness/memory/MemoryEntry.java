package tech.kayys.wayang.harness.memory;

import java.util.Objects;

/**
 * Represents a memory entry.
 *
 * <p>Its components capture `id`, `type`, `content`, `scope`, `metadata`.</p>
 *
 * @param id the id
 * @param type the type
 * @param content the content
 * @param scope the scope
 * @param metadata the metadata
 */


public record MemoryEntry(
        MemoryId id,
        MemoryType type,
        String content,
        MemoryScope scope,
        MemoryMetadata metadata
) {
    public MemoryEntry {
        id = id == null ? MemoryId.generate() : id;
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(content, "content");
        scope = scope == null ? MemoryScope.AGENT : scope;
        metadata = metadata == null ? MemoryMetadata.observed("system", "unknown", 1.0) : metadata;
    }

    public static MemoryEntry fact(String content, MemoryScope scope) {
        return new MemoryEntry(MemoryId.generate(), MemoryType.FACT, content, scope, MemoryMetadata.observed("agent", "exec", 1.0));
    }
}
