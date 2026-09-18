package tech.kayys.wayang.harness.journal;

import tech.kayys.wayang.harness.consistency.state.ExecutionId;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory thread-safe implementation of {@link SnapshotStore}.
 */
public class InMemorySnapshotStore implements SnapshotStore {

    private final Map<ExecutionId, ExecutionSnapshot> store = new ConcurrentHashMap<>();

    @Override
    public void save(ExecutionSnapshot snapshot) {
        Objects.requireNonNull(snapshot, "ExecutionSnapshot cannot be null");
        store.put(snapshot.executionId(), snapshot);
    }

    @Override
    public Optional<ExecutionSnapshot> latest(ExecutionId executionId) {
        Objects.requireNonNull(executionId, "ExecutionId cannot be null");
        return Optional.ofNullable(store.get(executionId));
    }
}
