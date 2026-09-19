package tech.kayys.wayang.execution.workspace;

import java.util.Objects;
import java.util.UUID;

/**
 * Immutable identifier for a workspace.
 */
public record WorkspaceId(String value) {

    public WorkspaceId {
        Objects.requireNonNull(value, "WorkspaceId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("WorkspaceId cannot be blank");
        }
        value = value.trim();
    }

    public static WorkspaceId of(String value) {
        return new WorkspaceId(value);
    }

    public static WorkspaceId generate() {
        return new WorkspaceId("ws-" + UUID.randomUUID());
    }
}
