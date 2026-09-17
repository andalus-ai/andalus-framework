package tech.kayys.wayang.harness.execution.checkpoint;

import tech.kayys.wayang.harness.execution.state.ExecutionId;
import tech.kayys.wayang.harness.execution.state.ExecutionState;

import java.util.Optional;

/**
 * Storage and lifecycle manager for execution checkpoints.
 */
public interface CheckpointManager {

    CheckpointId create(ExecutionState state);

    Optional<Checkpoint> load(CheckpointId id);

    Optional<Checkpoint> latest(ExecutionId executionId);

    void delete(CheckpointId id);
}
