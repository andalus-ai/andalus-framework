package tech.kayys.andalus.harness.selection;

import java.util.Objects;

/**
 * Represents a candidate that was evaluated and excluded from the selection result.
 */
public record CandidateExclusion(
        String candidateId,
        ExclusionReason reason,
        String explanation
) {
    public CandidateExclusion {
        Objects.requireNonNull(candidateId, "candidateId is required");
        Objects.requireNonNull(reason, "reason is required");
        explanation = explanation == null ? "" : explanation;
    }

    public static CandidateExclusion of(String candidateId, ExclusionReason reason, String explanation) {
        return new CandidateExclusion(candidateId, reason, explanation);
    }
}
