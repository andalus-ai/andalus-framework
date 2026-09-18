package tech.kayys.wayang.harness.journal;

import tech.kayys.wayang.harness.consistency.state.ExecutionId;

/**
 * Recovers authoritative execution state from a snapshot and tail event replay.
 */
public interface ExecutionRecoveryEngine {

    SnapshotPayload recover(ExecutionId executionId);
}
