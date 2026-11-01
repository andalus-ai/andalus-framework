package tech.kayys.andalus.execution.checkpoint;

import java.util.List;
import java.util.Optional;

/**
 * Persistence port for immutable execution checkpoints.
 *
 * <p>Implementations must make each save atomic: a checkpoint is either fully
 * visible in both point lookup and execution history, or not visible at all.
 * For a given execution, accepted sequence numbers must be strictly increasing;
 * concurrent writers must not both commit the same or an older sequence.</p>
 *
 * <p>Implementations must preserve checkpoint payload bytes and manifest
 * integrity metadata exactly. A successful save must be durable according to
 * the adapter's documented durability level. In-memory implementations are
 * process-local test/development adapters and do not provide crash durability.</p>
 */
public interface CheckpointStore {

    /** Atomically appends a checkpoint, rejecting duplicate IDs and stale sequences. */
    void save(ExecutionCheckpoint checkpoint);

    Optional<ExecutionCheckpoint> get(String checkpointId);

    /** Returns the checkpoint with the greatest accepted sequence, if present. */
    Optional<ExecutionCheckpoint> getLatest(String executionId);

    /** Returns an immutable snapshot ordered by accepted checkpoint sequence. */
    List<ExecutionCheckpoint> list(String executionId);
}
