package tech.kayys.wayang.execution.mobility;

import java.time.Instant;
import java.util.Objects;

/**
 * Authoritative durable state of an execution in Wayang v4.32.
 */
public record ExecutionDurableState(
        ExecutionIdentity identity,
        ExecutionStatus status,
        ExecutionCursor cursor,
        long versionSequence,
        String versionDigest,
        Instant updatedAt
) {
    public ExecutionDurableState {
        Objects.requireNonNull(identity, "identity cannot be null");
        Objects.requireNonNull(status, "status cannot be null");
        Objects.requireNonNull(cursor, "cursor cannot be null");
        if (versionDigest == null) {
            versionDigest = "sha256:0000000000000000";
        }
        if (updatedAt == null) {
            updatedAt = Instant.now();
        }
    }

    public static ExecutionDurableState of(ExecutionIdentity identity, ExecutionCursor cursor) {
        return new ExecutionDurableState(
                identity,
                ExecutionStatus.CREATED,
                cursor,
                1L,
                "sha256:init",
                Instant.now()
        );
    }

    public ExecutionDurableState withStatus(ExecutionStatus newStatus) {
        return new ExecutionDurableState(
                identity,
                newStatus,
                cursor,
                versionSequence + 1,
                versionDigest,
                Instant.now()
        );
    }

    public ExecutionDurableState withCursor(ExecutionCursor newCursor, String newDigest) {
        return new ExecutionDurableState(
                identity,
                status,
                newCursor,
                versionSequence + 1,
                newDigest != null ? newDigest : versionDigest,
                Instant.now()
        );
    }
}
