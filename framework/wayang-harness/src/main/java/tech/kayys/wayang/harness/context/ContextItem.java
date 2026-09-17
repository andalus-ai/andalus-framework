package tech.kayys.wayang.harness.context;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record ContextItem(
        String id,
        ContextKind kind,
        String content,
        ContextSource source,
        ContextPriority priority,
        Map<String, Object> metadata
) {
    public ContextItem {
        id = id == null ? "item-" + UUID.randomUUID() : id;
        Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(content, "content");
        source = source == null ? ContextSource.of("system", "default") : source;
        priority = priority == null ? ContextPriority.NORMAL : priority;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static ContextItem of(ContextKind kind, String content) {
        return new ContextItem(null, kind, content, null, ContextPriority.NORMAL, Map.of());
    }

    public static ContextItem of(ContextKind kind, String content, ContextPriority priority) {
        return new ContextItem(null, kind, content, null, priority, Map.of());
    }
}
