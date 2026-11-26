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
        ExecutionCheckpoint checkpoint = byId.get(checkpointId);
        if (checkpoint == null) {
            return Optional.empty();
        }
        List<ExecutionCheckpoint> history = byExecution.get(checkpoint.executionId());
        if (history == null) {
            return Optional.empty();
        }
        // save() publishes the ID index before appending to history while holding
        // this monitor. Readers use the same monitor to avoid observing a partial commit.
        synchronized (history) {
            return history.stream()
                    .filter(candidate -> candidate.checkpointId().equals(checkpointId))
                    .findFirst();
        }
    }

    @Override
    public Optional<ExecutionCheckpoint> getLatest(String executionId) {
        List<ExecutionCheckpoint> history = byExecution.get(executionId);
        if (history == null) {
            return Optional.empty();
        }
        synchronized (history) {
            return history.isEmpty() ? Optional.empty() : Optional.of(history.get(history.size() - 1));
        }
    }

    @Override
    public List<ExecutionCheckpoint> list(String executionId) {
        List<ExecutionCheckpoint> history = byExecution.get(executionId);
        if (history == null) {
            return List.of();
        }
        synchronized (history) {
            return List.copyOf(history);
        }
    }
}
