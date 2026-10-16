package tech.kayys.andalus.harness.decision.trace;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * An individual entry in a decision trace.
 */
public record DecisionTraceEntry(
        String id,
        DecisionTraceEntryType type,
        Instant occurredAt,
        Optional<String> sourceEventReference,
        List<String> evidenceReferences,
        Optional<String> artifactReference,
        Map<String, Object> attributes
) {
    public DecisionTraceEntry {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id is required");
        }
        if (type == null) {
            throw new IllegalArgumentException("type is required");
        }
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }

        sourceEventReference = sourceEventReference == null
                ? Optional.empty() : sourceEventReference;
        evidenceReferences = evidenceReferences == null
                ? List.of() : List.copyOf(evidenceReferences);
        artifactReference = artifactReference == null
                ? Optional.empty() : artifactReference;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static DecisionTraceEntry of(String id, DecisionTraceEntryType type, Map<String, Object> attributes) {
        return new DecisionTraceEntry(
                id,
                type,
                Instant.now(),
                Optional.empty(),
                List.of(),
                Optional.empty(),
                attributes
        );
    }

    public static DecisionTraceEntry of(
            String id,
            DecisionTraceEntryType type,
            String sourceEventReference,
            List<String> evidenceReferences,
            Map<String, Object> attributes
    ) {
        return new DecisionTraceEntry(
                id,
                type,
                Instant.now(),
                Optional.ofNullable(sourceEventReference),
                evidenceReferences,
                Optional.empty(),
                attributes
        );
    }
}
