package tech.kayys.wayang.execution.core.mobility;

import tech.kayys.wayang.execution.mobility.workspace.WorkspaceEntry;
import tech.kayys.wayang.execution.mobility.workspace.WorkspaceSnapshot;
import tech.kayys.wayang.execution.mobility.workspace.WorkspaceStore;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory implementation of {@link WorkspaceStore}.
 */
public class InMemoryWorkspaceStore implements WorkspaceStore {

    private final Map<String, WorkspaceSnapshot> snapshots = new ConcurrentHashMap<>();

    @Override
    public WorkspaceSnapshot snapshot(String workspaceId) {
        Objects.requireNonNull(workspaceId, "workspaceId cannot be null");
        WorkspaceSnapshot snap = WorkspaceSnapshot.of(
                workspaceId,
                "sha256:ws-" + System.currentTimeMillis(),
                1024L,
                List.of(
                        WorkspaceEntry.directory("/workspace"),
                        WorkspaceEntry.file("/workspace/project.json", 1024L, "sha256:entry1")
                )
        );
        snapshots.put(workspaceId, snap);
        return snap;
    }

    @Override
    public void restore(WorkspaceSnapshot snapshot, String targetWorkspaceId) {
        Objects.requireNonNull(snapshot, "snapshot cannot be null");
        Objects.requireNonNull(targetWorkspaceId, "targetWorkspaceId cannot be null");
        snapshots.put(targetWorkspaceId, snapshot);
    }

    @Override
    public Optional<WorkspaceSnapshot> getLatest(String workspaceId) {
        if (workspaceId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(snapshots.get(workspaceId));
    }
}
