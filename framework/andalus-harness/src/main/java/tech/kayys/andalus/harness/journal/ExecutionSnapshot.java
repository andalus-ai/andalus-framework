package tech.kayys.andalus.harness.journal;

import tech.kayys.andalus.harness.consistency.state.ExecutionId;

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
