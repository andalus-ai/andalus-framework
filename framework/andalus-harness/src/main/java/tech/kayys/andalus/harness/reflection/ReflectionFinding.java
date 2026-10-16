package tech.kayys.andalus.harness.reflection;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Concise analytical assessment produced during reflection, linked to supporting evidence.
 */
public record ReflectionFinding(
        String id,
        ReflectionFindingType type,
        String summary,
        List<String> evidenceReferences,
        List<String> relatedObservationReferences,
        Map<String, Object> metadata
) {
    public ReflectionFinding {
        id = id == null || id.isBlank() ? UUID.randomUUID().toString() : id;
        Objects.requireNonNull(type, "type is required");
        if (summary == null || summary.isBlank()) {
            throw new IllegalArgumentException("summary is required");
        }
        evidenceReferences = evidenceReferences == null ? List.of() : List.copyOf(evidenceReferences);
        relatedObservationReferences = relatedObservationReferences == null ? List.of() : List.copyOf(relatedObservationReferences);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static ReflectionFinding of(
            ReflectionFindingType type,
            String summary,
            List<String> relatedObservationReferences
    ) {
        return new ReflectionFinding(
                UUID.randomUUID().toString(),
                type,
                summary,
                List.of(),
                relatedObservationReferences,
                Map.of()
        );
    }
}
