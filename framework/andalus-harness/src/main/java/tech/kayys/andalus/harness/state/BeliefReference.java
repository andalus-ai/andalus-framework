package tech.kayys.andalus.harness.state;

import java.util.Objects;

/**
 * Compact reference to a belief in an agent state snapshot.
 */
public record BeliefReference(
        String beliefId,
        String subject,
        BeliefStatus status,
        long revision
) {
    public BeliefReference {
        Objects.requireNonNull(beliefId, "beliefId is required");
        subject = subject == null ? "" : subject;
        Objects.requireNonNull(status, "status is required");
    }
}
