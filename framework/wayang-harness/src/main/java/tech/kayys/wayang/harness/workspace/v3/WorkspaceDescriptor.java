package tech.kayys.wayang.harness.workspace.v3;

import tech.kayys.wayang.harness.workspace.WorkspaceId;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Metadata descriptor for a managed workspace.
 */
public record WorkspaceDescriptor(
        WorkspaceId id,
        String name,
        WorkspaceMode mode,
        Path rootPath,
        Set<String> capabilities,
        Map<String, String> attributes
) {
    public WorkspaceDescriptor {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        mode = mode != null ? mode : WorkspaceMode.EPHEMERAL;
        capabilities = capabilities != null ? Set.copyOf(capabilities) : Set.of();
        attributes = attributes != null ? Map.copyOf(attributes) : Map.of();
    }
}
