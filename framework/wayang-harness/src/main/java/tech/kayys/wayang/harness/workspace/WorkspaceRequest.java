package tech.kayys.wayang.harness.workspace;

import java.util.Objects;

/**
 * Request to allocate or prepare a workspace.
 */
public record WorkspaceRequest(
        String name,
        WorkspaceType type,
        WorkspacePolicy policy,
        boolean ephemeral
) {

    public WorkspaceRequest {
        name = Objects.requireNonNull(name, "name");
        type = type == null ? WorkspaceType.EPHEMERAL : type;
        policy = policy == null ? WorkspacePolicy.readWrite() : policy;
    }

    public static WorkspaceRequest ephemeral(String name) {
        return new WorkspaceRequest(name, WorkspaceType.EPHEMERAL, WorkspacePolicy.readWrite(), true);
    }

    public static WorkspaceRequest local(String name) {
        return new WorkspaceRequest(name, WorkspaceType.LOCAL, WorkspacePolicy.readWrite(), false);
    }
}
