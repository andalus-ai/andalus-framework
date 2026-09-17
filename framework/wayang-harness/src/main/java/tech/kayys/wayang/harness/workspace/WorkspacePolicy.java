package tech.kayys.wayang.harness.workspace;

import java.util.Set;

/**
 * Access control and security policy applied to a workspace.
 */
public record WorkspacePolicy(
        boolean readOnly,
        boolean allowSubdirectories,
        long maxDiskSizeBytes,
        Set<String> forbiddenExtensions
) {

    public WorkspacePolicy {
        forbiddenExtensions = forbiddenExtensions == null ? Set.of() : Set.copyOf(forbiddenExtensions);
    }

    public static WorkspacePolicy readWrite() {
        return new WorkspacePolicy(false, true, 1024L * 1024L * 1024L, Set.of());
    }

    public static WorkspacePolicy readOnlyPolicy() {
        return new WorkspacePolicy(true, true, 1024L * 1024L * 1024L, Set.of());
    }
}
