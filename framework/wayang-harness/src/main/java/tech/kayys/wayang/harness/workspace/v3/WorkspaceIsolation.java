package tech.kayys.wayang.harness.workspace.v3;

import tech.kayys.wayang.harness.capability.CapabilityScope;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Isolation boundary and execution constraints applied to a workspace.
 */
public record WorkspaceIsolation(
        IsolationLevel level,
        Collection<CapabilityScope> scopes
) {
    /**
     * Enumerates the isolation level values used by the Wayang framework.
     */

    public enum IsolationLevel {
        NONE,
        PROCESS,
        CONTAINER,
        VM,
        REMOTE
    }

    public WorkspaceIsolation {
        level = level != null ? level : IsolationLevel.PROCESS;
        scopes = scopes != null ? List.copyOf(scopes) : List.of();
    }

    public static WorkspaceIsolation processIsolated() {
        return new WorkspaceIsolation(IsolationLevel.PROCESS, List.of());
    }
}
