package tech.kayys.wayang.execution.core.checkpoint;

import tech.kayys.wayang.execution.checkpoint.CheckpointStore;
import tech.kayys.wayang.execution.checkpoint.ExecutionCheckpoint;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryExecutionCheckpointStore implements CheckpointStore {

    private final Map<String, ExecutionCheckpoint> byId = new ConcurrentHashMap<>();
    private final Map<String, List<ExecutionCheckpoint>> byExecution = new ConcurrentHashMap<>();

    @Override
    public void save(ExecutionCheckpoint checkpoint) {
        Objects.requireNonNull(checkpoint, "checkpoint cannot be null");

        // Sequence monotonicity check
        List<ExecutionCheckpoint> history = byExecution.computeIfAbsent(checkpoint.executionId(), k -> new CopyOnWriteArrayList<>());
        if (!history.isEmpty()) {
            ExecutionCheckpoint latest = history.get(history.size() - 1);
            if (checkpoint.sequence() <= latest.sequence()) {
                throw new IllegalArgumentException("Checkpoint sequence " + checkpoint.sequence() + " must be greater than previous " + latest.sequence());
            }
        }

        byId.put(checkpoint.checkpointId(), checkpoint);
        history.add(checkpoint);
    }

    @Override
    public Optional<ExecutionCheckpoint> get(String checkpointId) {
        return Optional.ofNullable(byId.get(checkpointId));
    }

    @Override
    public Optional<ExecutionCheckpoint> getLatest(String executionId) {
        List<ExecutionCheckpoint> history = byExecution.get(executionId);
        if (history == null || history.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(history.get(history.size() - 1));
    }

    @Override
    public List<ExecutionCheckpoint> list(String executionId) {
        List<ExecutionCheckpoint> history = byExecution.get(executionId);
        return history == null ? List.of() : List.copyOf(history);
    }
}
