package tech.kayys.wayang.harness.workspace.v3;

import tech.kayys.wayang.harness.workspace.WorkspaceId;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable snapshot of a workspace state at a point in time in v3.1.
 */
public record WorkspaceSnapshot(
        SnapshotId id,
        WorkspaceId workspaceId,
        String fingerprint,
        long totalBytes,
        int fileCount,
        Instant createdAt,
        Map<String, String> metadata
) {
    public WorkspaceSnapshot {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(workspaceId, "workspaceId");
        fingerprint = fingerprint != null ? fingerprint : "fp-empty";
        createdAt = createdAt != null ? createdAt : Instant.now();
        metadata = metadata != null ? Map.copyOf(metadata) : Map.of();
    }

    public static WorkspaceSnapshot empty(WorkspaceId workspaceId) {
        return new WorkspaceSnapshot(
                SnapshotId.generate(),
                workspaceId,
                "fp-0",
                0L,
                0,
                Instant.now(),
                Map.of()
        );
    }
}
