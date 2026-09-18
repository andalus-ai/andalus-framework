package tech.kayys.wayang.harness.journal;

import tech.kayys.wayang.harness.consistency.state.ExecutionId;

import java.util.Optional;

/**
 * Storage SPI for persisting and loading execution snapshots.
 */
public interface SnapshotStore {

    void save(ExecutionSnapshot snapshot);

    Optional<ExecutionSnapshot> latest(ExecutionId executionId);
}
