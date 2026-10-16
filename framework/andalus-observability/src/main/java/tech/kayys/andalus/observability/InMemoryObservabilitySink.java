package tech.kayys.andalus.observability;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

public class InMemoryObservabilitySink implements ObservabilitySink {

    private final List<ObservationEvent> events = new CopyOnWriteArrayList<>();

    @Override
    public void publish(ObservationEvent event) {
        if (event != null && supports(event)) {
            events.add(event);
        }
    }

    public List<ObservationEvent> getEvents() {
        return List.copyOf(events);
    }

    public List<ObservationEvent> findByExecutionId(String executionId) {
        if (executionId == null) {
            return List.of();
        }
        return events.stream()
                .filter(e -> executionId.equals(e.executionId()))
                .toList();
    }

    public List<ObservationEvent> findByType(String type) {
        if (type == null) {
            return List.of();
        }
        return events.stream()
                .filter(e -> type.equals(e.type()))
                .toList();
    }

    public List<ObservationEvent> filter(Predicate<ObservationEvent> predicate) {
        if (predicate == null) {
            return getEvents();
        }
        return events.stream()
                .filter(predicate)
                .toList();
    }

    public void clear() {
        events.clear();
    }

    @Override
    public void close() {
        clear();
    }
}
