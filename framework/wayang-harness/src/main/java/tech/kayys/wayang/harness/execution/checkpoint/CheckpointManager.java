package tech.kayys.wayang.harness.execution.checkpoint;

import tech.kayys.wayang.harness.execution.state.ExecutionId;
import tech.kayys.wayang.harness.execution.state.ExecutionState;
import tech.kayys.wayang.harness.journal.JournalPosition;

import java.util.Optional;

/**
 * Storage and lifecycle manager for execution checkpoints.
 */
public interface CheckpointManager {

    /**
     * Legacy checkpoint creation. Retained for backward compatibility.
     */
    CheckpointId create(ExecutionState state);

    /**
     * Creates a checkpoint at an explicit journal position.
     */
    default CheckpointId create(ExecutionState state, JournalPosition position) {
        return create(state);
    }

    Optional<Checkpoint> load(CheckpointId id);

    Optional<Checkpoint> latest(ExecutionId executionId);

    void delete(CheckpointId id);
}
