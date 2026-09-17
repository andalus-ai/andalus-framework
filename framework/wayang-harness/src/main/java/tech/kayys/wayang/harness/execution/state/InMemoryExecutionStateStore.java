package tech.kayys.wayang.harness.execution.state;

import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of {@link ExecutionStateStore} with optimistic concurrency control.
 */
public class InMemoryExecutionStateStore implements ExecutionStateStore {

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
            if (current == null) {
                return state;
            }
            if (current.version() != expectedVersion) {
                throw new ConcurrentModificationException("Optimistic lock error for execution " + state.executionId().value() +
                        ": expected version " + expectedVersion + " but was " + current.version());
            }
            return state;
        });
    }
}
