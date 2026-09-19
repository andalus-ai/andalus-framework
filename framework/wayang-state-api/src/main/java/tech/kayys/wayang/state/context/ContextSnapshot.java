package tech.kayys.wayang.state.context;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Context snapshot capturing references to fragments without duplicating heavy payloads.
 */
public record ContextSnapshot(
        ContextSnapshotId id,
        String sessionId,
        String taskId,
        List<ContextFragmentRef> fragments,
        ContextVersion version,
        Instant timestamp,
        Map<String, Object> metadata
) {
    public ContextSnapshot {
        Objects.requireNonNull(id, "id cannot be null");
        fragments = fragments == null ? List.of() : List.copyOf(fragments);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static ContextSnapshot of(ContextSnapshotId id, String sessionId, String taskId, List<ContextFragmentRef> fragments, ContextVersion version) {
        return new ContextSnapshot(id, sessionId, taskId, fragments, version, Instant.now(), Map.of());
    }
}
