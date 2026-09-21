package tech.kayys.andalus.harness.consistency.state;

import tech.kayys.andalus.harness.consistency.checkpoint.CheckpointId;

import java.time.Instant;
import java.util.Optional;

/**
 * Authoritative record of execution state with optimistic versioning.
 */
public interface ExecutionRecord {

    ExecutionId id();

    ExecutionState state();

    long version();

    Instant createdAt();

    Instant updatedAt();

    Optional<CheckpointId> checkpoint();

    Optional<FailureInfo> failure();
}
