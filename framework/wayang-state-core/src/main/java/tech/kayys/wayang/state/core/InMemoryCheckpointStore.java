package tech.kayys.wayang.state.core;

import tech.kayys.wayang.state.checkpoint.Checkpoint;
import tech.kayys.wayang.state.checkpoint.CheckpointId;
import tech.kayys.wayang.state.checkpoint.CheckpointStore;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryCheckpointStore implements CheckpointStore {

    private final Map<CheckpointId, Checkpoint> byId = new ConcurrentHashMap<>();
    private final Map<String, List<Checkpoint>> byExecution = new ConcurrentHashMap<>();

    @Override
    public void save(Checkpoint checkpoint) {
        Objects.requireNonNull(checkpoint, "checkpoint cannot be null");
        byId.put(checkpoint.id(), checkpoint);
        byExecution.computeIfAbsent(checkpoint.executionId(), k -> new CopyOnWriteArrayList<>()).add(checkpoint);
    }

    @Override
    public Optional<Checkpoint> get(CheckpointId id) {
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public Optional<Checkpoint> getLatest(String executionId) {
        List<Checkpoint> list = byExecution.get(executionId);
        if (list == null || list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(list.get(list.size() - 1));
    }

    @Override
    public List<Checkpoint> list(String executionId) {
        List<Checkpoint> list = byExecution.get(executionId);
        return list == null ? List.of() : List.copyOf(list);
    }
}
