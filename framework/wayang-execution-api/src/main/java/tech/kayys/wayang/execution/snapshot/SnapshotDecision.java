package tech.kayys.wayang.execution.snapshot;

import java.util.Objects;

/**
 * Outcome of evaluating a snapshot capture request against isolation rules.
 */
public record SnapshotDecision(
        boolean allowed,
        String reason
) {

    public SnapshotDecision {
        reason = reason != null ? reason : "";
    }

    public static SnapshotDecision allow(String reason) {
        return new SnapshotDecision(true, reason);
    }

    public static SnapshotDecision deny(String reason) {
        return new SnapshotDecision(false, reason);
    }
}
