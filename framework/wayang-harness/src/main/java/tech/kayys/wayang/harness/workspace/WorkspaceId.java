package tech.kayys.wayang.harness.workspace;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique identifier for a workspace execution environment.
 */
public record WorkspaceId(String value) {

    public WorkspaceId {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("Workspace id must not be blank");
        }
    }

    public static WorkspaceId of(String value) {
        return new WorkspaceId(value);
    }

    public static WorkspaceId generate() {
        return new WorkspaceId("ws-" + UUID.randomUUID());
    }
}
