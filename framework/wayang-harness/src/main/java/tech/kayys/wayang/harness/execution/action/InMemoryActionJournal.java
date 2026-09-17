package tech.kayys.wayang.harness.execution.action;

import tech.kayys.wayang.harness.execution.state.ExecutionId;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryActionJournal implements ActionJournal {

    private final Map<ActionId, ActionRecord> records = new ConcurrentHashMap<>();
    private final Map<ExecutionId, List<ActionRecord>> executionJournals = new ConcurrentHashMap<>();

    @Override
    public void started(ActionRecord action) {
        Objects.requireNonNull(action, "action");
        records.put(action.id(), action);
        executionJournals.computeIfAbsent(action.executionId(), k -> new CopyOnWriteArrayList<>()).add(action);
    }

    @Override
    public void completed(ActionId id, String output) {
        Objects.requireNonNull(id, "id");
        ActionRecord current = records.get(id);
        if (current == null) {
            throw new NoSuchElementException("Action record not found: " + id.value());
        }
        ActionRecord updated = current.complete(output);
        records.put(id, updated);
        updateHistory(updated);
    }

    @Override
    public void failed(ActionId id, String error) {
        Objects.requireNonNull(id, "id");
        ActionRecord current = records.get(id);
        if (current == null) {
            throw new NoSuchElementException("Action record not found: " + id.value());
        }
        ActionRecord updated = current.fail(error);
        records.put(id, updated);
        updateHistory(updated);
    }

    @Override
    public Optional<ActionRecord> find(ActionId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(records.get(id));
    }

    @Override
    public List<ActionRecord> history(ExecutionId executionId) {
        Objects.requireNonNull(executionId, "executionId");
        List<ActionRecord> list = executionJournals.get(executionId);
        return list != null ? List.copyOf(list) : List.of();
    }

    private void updateHistory(ActionRecord updated) {
        List<ActionRecord> list = executionJournals.get(updated.executionId());
        if (list != null) {
            list.removeIf(r -> r.id().equals(updated.id()));
            list.add(updated);
        }
    }
}
