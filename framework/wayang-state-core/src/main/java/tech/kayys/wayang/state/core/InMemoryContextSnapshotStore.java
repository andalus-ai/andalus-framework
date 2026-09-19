package tech.kayys.wayang.state.core;

import tech.kayys.wayang.state.context.ContextSnapshot;
import tech.kayys.wayang.state.context.ContextSnapshotId;
import tech.kayys.wayang.state.context.ContextSnapshotStore;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryContextSnapshotStore implements ContextSnapshotStore {

    private final Map<ContextSnapshotId, ContextSnapshot> byId = new ConcurrentHashMap<>();
    private final Map<String, List<ContextSnapshot>> byTask = new ConcurrentHashMap<>();
    private final Map<String, List<ContextSnapshot>> bySession = new ConcurrentHashMap<>();

    @Override
    public void save(ContextSnapshot snapshot) {
        Objects.requireNonNull(snapshot, "snapshot cannot be null");
        byId.put(snapshot.id(), snapshot);
        if (snapshot.taskId() != null) {
            byTask.computeIfAbsent(snapshot.taskId(), k -> new CopyOnWriteArrayList<>()).add(snapshot);
        }
        if (snapshot.sessionId() != null) {
            bySession.computeIfAbsent(snapshot.sessionId(), k -> new CopyOnWriteArrayList<>()).add(snapshot);
        }
    }

    @Override
    public Optional<ContextSnapshot> get(ContextSnapshotId id) {
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public Optional<ContextSnapshot> getLatestForTask(String taskId) {
        List<ContextSnapshot> list = byTask.get(taskId);
        if (list == null || list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(list.get(list.size() - 1));
    }

    @Override
    public List<ContextSnapshot> listForSession(String sessionId) {
        List<ContextSnapshot> list = bySession.get(sessionId);
        return list == null ? List.of() : List.copyOf(list);
    }
}
