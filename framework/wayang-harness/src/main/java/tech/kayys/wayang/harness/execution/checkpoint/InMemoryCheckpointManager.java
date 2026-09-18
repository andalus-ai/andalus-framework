package tech.kayys.wayang.harness.execution.checkpoint;

import tech.kayys.wayang.harness.execution.state.ExecutionId;
import tech.kayys.wayang.harness.execution.state.ExecutionState;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Provides in memory checkpoint manager behavior for the Wayang framework.
 */


public class InMemoryCheckpointManager implements CheckpointManager {

    private final Map<CheckpointId, Checkpoint> checkpoints = new ConcurrentHashMap<>();
    private final Map<ExecutionId, List<CheckpointId>> executionCheckpoints = new ConcurrentHashMap<>();

    @Override
    public CheckpointId create(ExecutionState state) {
        Objects.requireNonNull(state, "state");
        Checkpoint cp = Checkpoint.of(state);
        checkpoints.put(cp.id(), cp);
        executionCheckpoints.computeIfAbsent(state.executionId(), k -> new CopyOnWriteArrayList<>()).add(cp.id());
        return cp.id();
    }

    @Override
    public Optional<Checkpoint> load(CheckpointId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(checkpoints.get(id));
    }

    @Override
    public Optional<Checkpoint> latest(ExecutionId executionId) {
        Objects.requireNonNull(executionId, "executionId");
        List<CheckpointId> list = executionCheckpoints.get(executionId);
        if (list == null || list.isEmpty()) {
            return Optional.empty();
        }
        CheckpointId lastId = list.get(list.size() - 1);
        return load(lastId);
    }

    @Override
    public void delete(CheckpointId id) {
        Objects.requireNonNull(id, "id");
        Checkpoint cp = checkpoints.remove(id);
        if (cp != null) {
            List<CheckpointId> list = executionCheckpoints.get(cp.executionId());
            if (list != null) {
                list.remove(id);
            }
        }
    }
}
