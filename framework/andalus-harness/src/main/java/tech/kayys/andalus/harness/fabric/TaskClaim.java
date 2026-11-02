package tech.kayys.andalus.harness.fabric;

import java.time.Instant;
import java.util.Objects;

/** A time-bounded claim on an assignment; token.workerId is the current queue claimant. */
public record TaskClaim(ExecutionAssignment assignment, TaskClaimToken token,
                       Instant claimedAt, Instant visibilityExpiresAt) {
    public TaskClaim {
        Objects.requireNonNull(assignment, "assignment cannot be null");
        Objects.requireNonNull(token, "token cannot be null");
        Objects.requireNonNull(claimedAt, "claimedAt cannot be null");
        Objects.requireNonNull(visibilityExpiresAt, "visibilityExpiresAt cannot be null");
        if (!assignment.id().equals(token.assignmentId())) {
            throw new IllegalArgumentException("Claim token does not match assignment");
        }
        if (!visibilityExpiresAt.isAfter(claimedAt)) {
            throw new IllegalArgumentException("visibility expiry must be after claim time");
        }
    }
}
