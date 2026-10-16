package tech.kayys.andalus.harness.state;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * A working, revisable interpretation of evidence held by an agent during execution.
 */
public record Belief(
        String id,
        String subject,
        String proposition,
        BeliefStatus status,
        List<BeliefEvidenceReference> evidence,
        Optional<String> supersedesBeliefId,
        long revision,
        Instant createdAt,
        Instant updatedAt,
        Map<String, Object> metadata
) {
    public Belief {
        id = id == null || id.isBlank() ? UUID.randomUUID().toString() : id;
        subject = subject == null ? "" : subject;
        if (proposition == null || proposition.isBlank()) {
            throw new IllegalArgumentException("proposition is required");
        }
        Objects.requireNonNull(status, "status is required");
        evidence = evidence == null ? List.of() : List.copyOf(evidence);
        supersedesBeliefId = supersedesBeliefId == null ? Optional.empty() : supersedesBeliefId;
        createdAt = createdAt == null ? Instant.now() : createdAt;
        updatedAt = updatedAt == null ? createdAt : updatedAt;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static Belief of(String subject, String proposition, List<BeliefEvidenceReference> evidence) {
        return new Belief(
                UUID.randomUUID().toString(),
                subject,
                proposition,
                BeliefStatus.ACTIVE,
                evidence,
                Optional.empty(),
                1L,
                Instant.now(),
                Instant.now(),
                Map.of()
        );
    }

    public Belief withStatus(BeliefStatus newStatus) {
        return new Belief(
                id, subject, proposition, newStatus, evidence, supersedesBeliefId,
                revision + 1, createdAt, Instant.now(), metadata
        );
    }

    public Belief withEvidence(List<BeliefEvidenceReference> additionalEvidence) {
        List<BeliefEvidenceReference> merged = new ArrayList<>(evidence);
        if (additionalEvidence != null) {
            merged.addAll(additionalEvidence);
        }
        return new Belief(
                id, subject, proposition, status, merged, supersedesBeliefId,
                revision + 1, createdAt, Instant.now(), metadata
        );
    }

    public Belief supersede(String newProposition, List<BeliefEvidenceReference> newEvidence) {
        return new Belief(
                UUID.randomUUID().toString(),
                subject,
                newProposition,
                BeliefStatus.ACTIVE,
                newEvidence,
                Optional.of(id),
                1L,
                Instant.now(),
                Instant.now(),
                Map.of("supersedesRevision", revision)
        );
    }

    public BeliefReference toReference() {
        return new BeliefReference(id, subject, status, revision);
    }
}
