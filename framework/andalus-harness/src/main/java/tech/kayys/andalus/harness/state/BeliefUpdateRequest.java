package tech.kayys.andalus.harness.state;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Request payload for creating, updating, or retracting an agent belief.
 */
public record BeliefUpdateRequest(
        String executionId,
        Optional<String> existingBeliefId,
        String subject,
        String proposition,
        List<BeliefEvidenceReference> evidence,
        BeliefUpdateReason reason,
        long expectedRevision,
        Map<String, Object> metadata
) {
    public BeliefUpdateRequest {
        if (executionId == null || executionId.isBlank()) {
            throw new IllegalArgumentException("executionId is required");
        }
        if (proposition == null || proposition.isBlank()) {
            throw new IllegalArgumentException("proposition is required");
        }
        subject = subject == null ? "" : subject;
        Objects.requireNonNull(reason, "reason is required");

        existingBeliefId = existingBeliefId == null ? Optional.empty() : existingBeliefId;
        evidence = evidence == null ? List.of() : List.copyOf(evidence);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static BeliefUpdateRequest create(
            String executionId,
            String subject,
            String proposition,
            List<BeliefEvidenceReference> evidence
    ) {
        return new BeliefUpdateRequest(
                executionId,
                Optional.empty(),
                subject,
                proposition,
                evidence,
                BeliefUpdateReason.NEW_EVIDENCE,
                0L,
                Map.of()
        );
    }

    public static BeliefUpdateRequest update(
            String executionId,
            String beliefId,
            String subject,
            String proposition,
            List<BeliefEvidenceReference> evidence,
            BeliefUpdateReason reason,
            long expectedRevision
    ) {
        return new BeliefUpdateRequest(
                executionId,
                Optional.of(beliefId),
                subject,
                proposition,
                evidence,
                reason,
                expectedRevision,
                Map.of()
        );
    }

    public static BeliefUpdateRequest retract(
            String executionId,
            String beliefId,
            long expectedRevision
    ) {
        return new BeliefUpdateRequest(
                executionId,
                Optional.of(beliefId),
                "",
                "RETRACTED",
                List.of(),
                BeliefUpdateReason.EXPLICIT_RETRACTION,
                expectedRevision,
                Map.of()
        );
    }
}
