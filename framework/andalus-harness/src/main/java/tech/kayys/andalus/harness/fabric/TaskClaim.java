package tech.kayys.andalus.harness.fabric;

import java.time.Instant;
import java.util.Objects;

/** A time-bounded claim on an assignment. */
public record TaskClaim(ExecutionAssignment assignment, TaskClaimToken token,
                       Instant claimedAt, Instant visibilityExpiresAt) {
    public TaskClaim {
        Objects.requireNonNull(assignment, "assignment cannot be null");
        Objects.requireNonNull(token, "token cannot be null");
        Objects.requireNonNull(claimedAt, "claimedAt cannot be null");
        Objects.requireNonNull(visibilityExpiresAt, "visibilityExpiresAt cannot be null");
        if (!assignment.id().equals(token.assignmentId())
                || !assignment.workerId().equals(token.workerId())) {
            throw new IllegalArgumentException("Claim token does not match assignment owner");
        }
        if (!visibilityExpiresAt.isAfter(claimedAt)) {
            throw new IllegalArgumentException("visibility expiry must be after claim time");
        }
    }
}
