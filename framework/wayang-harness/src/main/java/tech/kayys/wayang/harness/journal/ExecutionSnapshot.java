package tech.kayys.wayang.harness.journal;

import tech.kayys.wayang.harness.consistency.state.ExecutionId;

import java.time.Instant;

/**
 * Point-in-time snapshot of an execution for fast recovery without full journal replays.
 */
public interface ExecutionSnapshot {

    SnapshotId id();

    ExecutionId executionId();

    JournalPosition position();

    SnapshotPayload payload();

    Instant createdAt();
}
