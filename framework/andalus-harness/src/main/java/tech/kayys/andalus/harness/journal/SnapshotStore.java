package tech.kayys.andalus.harness.journal;

import tech.kayys.andalus.harness.consistency.state.ExecutionId;

import java.util.Optional;

/**
 * Storage SPI for persisting and loading execution snapshots.
 */
public interface SnapshotStore {

    void save(ExecutionSnapshot snapshot);

    Optional<ExecutionSnapshot> latest(ExecutionId executionId);
}
