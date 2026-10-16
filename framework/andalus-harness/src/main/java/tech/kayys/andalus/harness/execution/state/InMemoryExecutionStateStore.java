package tech.kayys.andalus.harness.execution.state;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of {@link ExecutionStateStore} with optimistic concurrency control.
 */
public class InMemoryExecutionStateStore implements ExecutionStateStore, ExecutionStateDiscovery {

    private final Map<ExecutionId, ExecutionState> store = new ConcurrentHashMap<>();

    @Override
    public Optional<ExecutionState> load(ExecutionId executionId) {
        Objects.requireNonNull(executionId, "executionId");
        return Optional.ofNullable(store.get(executionId));
    }

    @Override
    public void save(ExecutionState state, long expectedVersion) {
        Objects.requireNonNull(state, "state");
        store.compute(state.executionId(), (id, current) -> {
            if (current != null) {
                if (current.version() != expectedVersion) {
                    throw new ConcurrentModificationException("Optimistic lock error for execution " + state.executionId().value() +
                            ": expected version " + expectedVersion + " but was " + current.version());
                }
                if (state.version() <= current.version()) {
                    throw new ConcurrentModificationException("Invalid version progression for execution " + state.executionId().value() +
                            ": new version " + state.version() + " must be greater than current version " + current.version());
                }
            }
            return state;
        });
    }

    @Override
    public Collection<ExecutionId> findWaitingExecutions() {
        return store.values().stream()
                .filter(s -> s.status() == ExecutionStatus.WAITING)
                .map(ExecutionState::executionId)
                .toList();
    }
}
