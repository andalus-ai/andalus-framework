package tech.kayys.andalus.harness.journal;

import tech.kayys.andalus.harness.consistency.state.ExecutionId;

/**
 * Recovers authoritative execution state from a snapshot and tail event replay.
 */
public interface ExecutionRecoveryEngine {

    SnapshotPayload recover(ExecutionId executionId);
}
