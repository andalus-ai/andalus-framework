package tech.kayys.wayang.harness.workspace;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Point-in-time snapshot metadata of a workspace state.
 */
public record WorkspaceSnapshot(
        String revision,
        String fingerprint,
        Instant timestamp,
        long totalBytes,
        int fileCount,
        Map<String, String> metadata
) {

    public WorkspaceSnapshot {
        Objects.requireNonNull(revision, "revision");
        timestamp = timestamp == null ? Instant.now() : timestamp;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static WorkspaceSnapshot empty() {
        return new WorkspaceSnapshot("rev-0", "empty", Instant.now(), 0L, 0, Map.of());
    }
}
