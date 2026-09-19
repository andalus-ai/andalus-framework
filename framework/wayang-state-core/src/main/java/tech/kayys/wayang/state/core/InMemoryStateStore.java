package tech.kayys.wayang.state.core;

import tech.kayys.wayang.state.*;

import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Thread-safe in-memory implementation of StateStore with version tracking.
 */
public class InMemoryStateStore implements StateStore {

    private final Map<StateKey, List<StateSnapshot>> storage = new ConcurrentHashMap<>();

    @Override
    public <T> StateSnapshot save(StateKey key, String executionId, T state) {
        Objects.requireNonNull(key, "key cannot be null");
        Objects.requireNonNull(state, "state cannot be null");

        List<StateSnapshot> history = storage.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>());
        long nextSequence = history.size() + 1L;
        StateVersion version = new StateVersion(nextSequence, "1.0");

        byte[] serialized;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(state);
            oos.flush();
            serialized = baos.toByteArray();
        } catch (IOException e) {
            serialized = state.toString().getBytes();
        }

        StatePayload payload = StatePayload.of(serialized, "application/java-serialized-object", state);
        StateSnapshot snapshot = StateSnapshot.of(SnapshotId.random(), executionId, version, payload, Map.of());
        history.add(snapshot);
        return snapshot;
    }

    @Override
    public <T> Optional<T> load(StateKey key, StateVersion version, Class<T> type) {
        List<StateSnapshot> history = storage.get(key);
        if (history == null) {
            return Optional.empty();
        }

        return history.stream()
                .filter(s -> s.version().sequence() == version.sequence())
                .findFirst()
                .flatMap(s -> s.payload().unwrap(type));
    }

    @Override
    public Optional<StateSnapshot> getLatest(StateKey key) {
        List<StateSnapshot> history = storage.get(key);
        if (history == null || history.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(history.get(history.size() - 1));
    }

    @Override
    public List<StateSnapshot> getHistory(StateKey key) {
        List<StateSnapshot> history = storage.get(key);
        if (history == null) {
            return List.of();
        }
        return List.copyOf(history);
    }
}
