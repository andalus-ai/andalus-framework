package tech.kayys.wayang.harness.consistency.checkpoint;

import tech.kayys.wayang.harness.consistency.state.ExecutionId;

import java.time.Instant;

/**
 * Checkpoint capturing minimum durable information needed to restore or continue execution.
 */
public interface Checkpoint {

    CheckpointId id();

    ExecutionId executionId();

    long sequence();

    CheckpointManifest manifest();

    Instant createdAt();
}
