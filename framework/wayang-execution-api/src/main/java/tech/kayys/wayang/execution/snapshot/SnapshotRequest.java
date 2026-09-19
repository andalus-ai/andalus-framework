package tech.kayys.wayang.execution.snapshot;

import tech.kayys.wayang.execution.sandbox.SandboxId;

import java.util.Objects;
import java.util.Set;

/**
 * Request to capture a snapshot of a sandbox execution state.
 */
public record SnapshotRequest(
        SandboxId sandboxId,
        Set<String> includePaths,
        Set<String> excludePaths,
        boolean includeSecrets
) {

    public SnapshotRequest {
        Objects.requireNonNull(sandboxId, "sandboxId cannot be null");
        includePaths = includePaths != null ? Set.copyOf(includePaths) : Set.of();
        excludePaths = excludePaths != null ? Set.copyOf(excludePaths) : Set.of();
    }

    public static SnapshotRequest workspaceOnly(SandboxId sandboxId) {
        return new SnapshotRequest(sandboxId, Set.of("workspace"), Set.of(), false);
    }
}
