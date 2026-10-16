package tech.kayys.andalus.harness.state;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * References supporting or contradicting evidence for a belief, maintaining provenance.
 */
public record BeliefEvidenceReference(
        String sourceId,
        EvidenceSourceType sourceType,
        Optional<String> revision,
        Optional<String> excerptReference,
        Instant observedAt,
        Map<String, Object> metadata
) {
    public BeliefEvidenceReference {
        if (sourceId == null || sourceId.isBlank()) {
            throw new IllegalArgumentException("sourceId is required");
        }
        Objects.requireNonNull(sourceType, "sourceType is required");

        revision = revision == null ? Optional.empty() : revision;
        excerptReference = excerptReference == null ? Optional.empty() : excerptReference;
        observedAt = observedAt == null ? Instant.now() : observedAt;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static BeliefEvidenceReference ofObservation(String observationId) {
        return new BeliefEvidenceReference(
                observationId,
                EvidenceSourceType.OBSERVATION,
                Optional.empty(),
                Optional.empty(),
                Instant.now(),
                Map.of()
        );
    }

    public static BeliefEvidenceReference ofKnowledge(String knowledgeId, String revision) {
        return new BeliefEvidenceReference(
                knowledgeId,
                EvidenceSourceType.KNOWLEDGE,
                Optional.ofNullable(revision),
                Optional.empty(),
                Instant.now(),
                Map.of()
        );
    }
}
