package tech.kayys.andalus.harness.selection;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Normalized descriptor of a candidate tool or skill evaluated during selection.
 */
public record CandidateDescriptor(
        String id,
        SelectionTarget target,
        Set<String> capabilities,
        String description,
        Map<String, Object> inputSchema,
        boolean enabled,
        Map<String, Object> metadata
) {
    public CandidateDescriptor {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(target, "target is required");
        capabilities = capabilities == null ? Set.of() : Set.copyOf(capabilities);
        description = description == null ? "" : description;
        inputSchema = inputSchema == null ? Map.of() : Map.copyOf(inputSchema);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static CandidateDescriptor ofTool(String id, Set<String> capabilities, String description) {
        return new CandidateDescriptor(id, SelectionTarget.TOOL, capabilities, description, Map.of(), true, Map.of());
    }

    public static CandidateDescriptor ofSkill(String id, Set<String> capabilities, String description) {
        return new CandidateDescriptor(id, SelectionTarget.SKILL, capabilities, description, Map.of(), true, Map.of());
    }
}
