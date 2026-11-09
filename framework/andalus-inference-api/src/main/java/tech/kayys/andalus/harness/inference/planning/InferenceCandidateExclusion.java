package tech.kayys.andalus.harness.inference.planning;

import java.util.Objects;

/**
 * Explains why a candidate model was excluded from the feasible candidate set.
 */
public record InferenceCandidateExclusion(
        String candidateId,
        String reason,
        String explanation
) {
    public InferenceCandidateExclusion {
        Objects.requireNonNull(candidateId, "candidateId is required");
        Objects.requireNonNull(reason, "reason is required");
        explanation = explanation == null ? "" : explanation;
    }

    public static InferenceCandidateExclusion of(String candidateId, String reason, String explanation) {
        return new InferenceCandidateExclusion(candidateId, reason, explanation);
    }
}
