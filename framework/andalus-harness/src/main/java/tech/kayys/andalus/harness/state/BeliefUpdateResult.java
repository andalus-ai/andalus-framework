package tech.kayys.andalus.harness.state;

import java.util.Objects;
import java.util.Optional;

/**
 * Result of attempting to create, update, or retract a belief.
 */
public record BeliefUpdateResult(
        BeliefUpdateStatus status,
        Optional<Belief> belief,
        long revision,
        String message
) {
    public BeliefUpdateResult {
        Objects.requireNonNull(status, "status is required");
        belief = belief == null ? Optional.empty() : belief;
        message = message == null ? "" : message;
    }

    public static BeliefUpdateResult created(Belief belief, long revision) {
        return new BeliefUpdateResult(BeliefUpdateStatus.CREATED, Optional.of(belief), revision, "Belief created");
    }

    public static BeliefUpdateResult updated(Belief belief, long revision) {
        return new BeliefUpdateResult(BeliefUpdateStatus.UPDATED, Optional.of(belief), revision, "Belief updated");
    }

    public static BeliefUpdateResult conflict(long currentRevision, String message) {
        return new BeliefUpdateResult(BeliefUpdateStatus.CONFLICT, Optional.empty(), currentRevision, message);
    }

    public static BeliefUpdateResult notFound(String beliefId) {
        return new BeliefUpdateResult(BeliefUpdateStatus.NOT_FOUND, Optional.empty(), 0L, "Belief not found: " + beliefId);
    }

    public static BeliefUpdateResult failed(String message) {
        return new BeliefUpdateResult(BeliefUpdateStatus.FAILED, Optional.empty(), 0L, message);
    }

    public boolean isSuccess() {
        return status.isSuccess();
    }
}
