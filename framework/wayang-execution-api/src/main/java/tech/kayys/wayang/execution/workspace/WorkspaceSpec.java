package tech.kayys.wayang.execution.workspace;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Declarative workspace specification for an execution sandbox.
 */
public record WorkspaceSpec(
        WorkspaceMode mode,
        Optional<WorkspaceId> existingWorkspace,
        Set<MountSpec> mounts
) {

    public WorkspaceSpec {
        mode = mode != null ? mode : WorkspaceMode.EPHEMERAL;
        existingWorkspace = existingWorkspace != null ? existingWorkspace : Optional.empty();
        mounts = mounts != null ? Set.copyOf(mounts) : Set.of();
    }

    public static WorkspaceSpec ephemeral() {
        return new WorkspaceSpec(WorkspaceMode.EPHEMERAL, Optional.empty(), Set.of());
    }

    public static WorkspaceSpec persistent(WorkspaceId workspaceId) {
        return new WorkspaceSpec(WorkspaceMode.PERSISTENT, Optional.of(workspaceId), Set.of());
    }

    public static WorkspaceSpec withMounts(WorkspaceMode mode, Set<MountSpec> mounts) {
        return new WorkspaceSpec(mode, Optional.empty(), mounts);
    }
}
