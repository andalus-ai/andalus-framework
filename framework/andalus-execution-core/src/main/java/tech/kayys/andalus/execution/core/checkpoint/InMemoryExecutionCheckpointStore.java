package tech.kayys.andalus.execution.core.checkpoint;

import tech.kayys.andalus.execution.checkpoint.CheckpointStore;
import tech.kayys.andalus.execution.checkpoint.ExecutionCheckpoint;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryExecutionCheckpointStore implements CheckpointStore {

    private final Map<String, ExecutionCheckpoint> byId = new ConcurrentHashMap<>();
    private final Map<String, List<ExecutionCheckpoint>> byExecution = new ConcurrentHashMap<>();

    @Override
    public void save(ExecutionCheckpoint checkpoint) {
        Objects.requireNonNull(checkpoint, "checkpoint cannot be null");

        List<ExecutionCheckpoint> history = byExecution.computeIfAbsent(
                checkpoint.executionId(), ignored -> new CopyOnWriteArrayList<>());

        // Sequence validation and insertion must be one atomic operation per execution.
        // Synchronizing on the per-execution history allows unrelated executions to progress
        // concurrently while preventing two writers from accepting the same next sequence.
        synchronized (history) {
            if (!history.isEmpty()) {
                ExecutionCheckpoint latest = history.get(history.size() - 1);
                if (checkpoint.sequence() <= latest.sequence()) {
                    throw new IllegalArgumentException(
                            "Checkpoint sequence " + checkpoint.sequence()
                                    + " must be greater than previous " + latest.sequence());
                }
            }

            ExecutionCheckpoint existing = byId.putIfAbsent(checkpoint.checkpointId(), checkpoint);
            if (existing != null) {
                throw new IllegalArgumentException(
                        "Checkpoint ID already exists: " + checkpoint.checkpointId());
            }

            history.add(checkpoint);
        }
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
