package tech.kayys.andalus.harness.reflection;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Concrete proposal suggesting an adjustment to a future action, plan, belief, or retained memory.
 */
public record ReflectionProposal(
        String id,
        ReflectionProposalType type,
        String description,
        List<String> supportingFindingIds,
        Optional<String> targetReference,
        Map<String, Object> metadata
) {
    public ReflectionProposal {
        id = id == null || id.isBlank() ? UUID.randomUUID().toString() : id;
        Objects.requireNonNull(type, "type is required");
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("description is required");
        }
        supportingFindingIds = supportingFindingIds == null ? List.of() : List.copyOf(supportingFindingIds);
        targetReference = targetReference == null ? Optional.empty() : targetReference;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static ReflectionProposal of(
            ReflectionProposalType type,
            String description,
            List<String> supportingFindingIds
    ) {
        return new ReflectionProposal(
                UUID.randomUUID().toString(),
                type,
                description,
                supportingFindingIds,
                Optional.empty(),
                Map.of()
        );
    }
}
