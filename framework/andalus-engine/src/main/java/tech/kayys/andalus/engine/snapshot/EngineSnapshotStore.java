package tech.kayys.andalus.engine.snapshot;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import tech.kayys.andalus.execution.ExecutionSnapshot;

/**
 * Engine-local persistence port for graph execution snapshots.
 *
 * <p>This contract is deliberately distinct from the Runtime-SPI
 * {@code CheckpointStore}, which persists semantic {@code AgentContext}
 * values. It is also distinct from the invocation checkpoint API, which
 * stores immutable {@code ExecutionCheckpoint} records.</p>
 *
 * <p>Implementations should provide durable storage when used for production
 * recovery. The default latest lookup derives its result from {@link #list()}
 * and never uses {@code null} to represent absence.</p>
 */
public interface EngineSnapshotStore {

    /** Saves an engine execution snapshot. */
    void save(ExecutionSnapshot snapshot);

    /** Restores the latest snapshot for an execution, if one exists. */
    ExecutionSnapshot restore(UUID executionId);

    /** Deletes snapshots for the given execution. */
    void delete(UUID executionId);

    /** Returns all snapshots known to this store. */
    List<ExecutionSnapshot> list();

    /**
     * Returns the latest snapshot for an execution.
     *
     * <p>The default implementation selects the greatest creation timestamp.
     * Stores with indexed lookup may override this method.</p>
     */
    default Optional<ExecutionSnapshot> getLatest(UUID executionId) {
        if (executionId == null) {
            return Optional.empty();
        }
        return list().stream()
                .filter(snapshot -> executionId.equals(snapshot.getExecutionId()))
                .max(Comparator.comparing(ExecutionSnapshot::getCreatedAt));
    }
}
