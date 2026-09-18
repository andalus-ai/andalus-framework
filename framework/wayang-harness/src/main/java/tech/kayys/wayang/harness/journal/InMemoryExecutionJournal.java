package tech.kayys.wayang.harness.journal;

import tech.kayys.wayang.harness.consistency.state.ExecutionId;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Thread-safe in-memory implementation of {@link ExecutionJournal}.
 */
public class InMemoryExecutionJournal implements ExecutionJournal {

    private final JournalId id;
    private final Map<ExecutionId, List<ExecutionEvent>> store = new ConcurrentHashMap<>();

    public InMemoryExecutionJournal() {
        this(JournalId.generate());
    }

    public InMemoryExecutionJournal(JournalId id) {
        this.id = Objects.requireNonNull(id, "JournalId cannot be null");
    }

    @Override
    public JournalId id() {
        return id;
    }

    @Override
    public synchronized void append(ExecutionEvent event) {
        Objects.requireNonNull(event, "ExecutionEvent cannot be null");
        List<ExecutionEvent> list = store.computeIfAbsent(event.executionId(), k -> new CopyOnWriteArrayList<>());
        list.add(event);
    }

    @Override
    public List<ExecutionEvent> read(ExecutionId executionId, JournalPosition from) {
        Objects.requireNonNull(executionId, "ExecutionId cannot be null");
        List<ExecutionEvent> list = store.getOrDefault(executionId, List.of());
        if (from == null || from.sequence() <= 0) {
            return List.copyOf(list);
        }
        return list.stream()
                .filter(e -> e.sequence().value() >= from.sequence())
                .toList();
    }

    @Override
    public JournalPosition position(ExecutionId executionId) {
        Objects.requireNonNull(executionId, "ExecutionId cannot be null");
        List<ExecutionEvent> list = store.getOrDefault(executionId, List.of());
        if (list.isEmpty()) {
            return JournalPosition.beginning();
        }
        return JournalPosition.of(list.get(list.size() - 1).sequence().value());
    }

    @Override
    public List<ExecutionEvent> history(ExecutionId executionId) {
        Objects.requireNonNull(executionId, "ExecutionId cannot be null");
        return List.copyOf(store.getOrDefault(executionId, List.of()));
    }
}
