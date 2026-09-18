package tech.kayys.wayang.harness.workspace.v3;

import tech.kayys.wayang.harness.workspace.Workspace;
import tech.kayys.wayang.harness.workspace.WorkspaceId;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe default in-memory implementation of {@link WorkspaceSnapshotManager}.
 */
public class DefaultWorkspaceSnapshotManager implements WorkspaceSnapshotManager {

    private final Map<SnapshotId, WorkspaceSnapshot> snapshots = new ConcurrentHashMap<>();

    @Override
    public WorkspaceSnapshot create(WorkspaceId workspaceId, SnapshotOptions options) {
        Objects.requireNonNull(workspaceId, "workspaceId");
        options = options != null ? options : SnapshotOptions.defaults();

        SnapshotId id = SnapshotId.generate();
        WorkspaceSnapshot snapshot = new WorkspaceSnapshot(
                id,
                workspaceId,
                "fp-" + System.currentTimeMillis(),
                1024L,
                1,
                Instant.now(),
                Map.of("note", options.note())
        );
        snapshots.put(id, snapshot);
        return snapshot;
    }

    @Override
    public Optional<WorkspaceSnapshot> find(SnapshotId snapshotId) {
        if (snapshotId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(snapshots.get(snapshotId));
    }

    @Override
    public void restore(SnapshotId snapshotId, Workspace destination) {
        Objects.requireNonNull(snapshotId, "snapshotId");
        Objects.requireNonNull(destination, "destination");
        if (!snapshots.containsKey(snapshotId)) {
            throw new IllegalArgumentException("Snapshot not found: " + snapshotId.value());
        }
    }

    @Override
    public void delete(SnapshotId snapshotId) {
        if (snapshotId != null) {
            snapshots.remove(snapshotId);
        }
    }
}
