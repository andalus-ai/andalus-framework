package tech.kayys.andalus.state;

import tech.kayys.andalus.execution.sandbox.SandboxId;

import java.time.Instant;
import java.util.Map;

/**
 * Immutable state snapshot representation at a point in execution.
 */
public interface StateSnapshot {
    SnapshotId id();
    String executionId();
    Instant timestamp();
    StateVersion version();
    StatePayload payload();
    Map<String, Object> metadata();

    static StateSnapshot of(SnapshotId id, String executionId, StateVersion version, StatePayload payload, Map<String, Object> metadata) {
        return new SimpleStateSnapshot(id, executionId, Instant.now(), version, payload, metadata == null ? Map.of() : Map.copyOf(metadata));
    }

    record SimpleStateSnapshot(
            SnapshotId id,
            String executionId,
            Instant timestamp,
            StateVersion version,
            StatePayload payload,
            Map<String, Object> metadata
    ) implements StateSnapshot {}
}
