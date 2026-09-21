package tech.kayys.andalus.execution.mobility;

import java.time.Instant;
import java.util.Objects;

/**
 * Audit record of a fenced state sequence transition.
 */
public record StateTransitionRecord(
        String executionId,
        long fromSequence,
        long toSequence,
        FencingToken fencingToken,
        Instant timestamp
) {
    public StateTransitionRecord {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(fencingToken, "fencingToken cannot be null");
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }
}
