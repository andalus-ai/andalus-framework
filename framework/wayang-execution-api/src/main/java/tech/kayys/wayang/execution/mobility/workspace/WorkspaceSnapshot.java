package tech.kayys.wayang.execution.mobility.workspace;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Portable, content-addressed snapshot of a workspace.
 */
public record WorkspaceSnapshot(
        String workspaceId,
        String digest,
        long size,
        List<WorkspaceEntry> entries,
        Instant capturedAt
) {
    public WorkspaceSnapshot {
        Objects.requireNonNull(workspaceId, "workspaceId cannot be null");
        Objects.requireNonNull(digest, "digest cannot be null");
        if (size < 0) {
            throw new IllegalArgumentException("size cannot be negative");
        }
        entries = entries == null ? List.of() : List.copyOf(entries);
        if (capturedAt == null) {
            capturedAt = Instant.now();
        }
    }

    public static WorkspaceSnapshot of(String workspaceId, String digest, long size, List<WorkspaceEntry> entries) {
        return new WorkspaceSnapshot(workspaceId, digest, size, entries, Instant.now());
    }
}
