package tech.kayys.andalus.harness.observability.replay;

import tech.kayys.andalus.harness.execution.state.ExecutionId;
import tech.kayys.andalus.harness.observability.event.EventFilter;
import tech.kayys.andalus.harness.observability.event.EventJournal;
import tech.kayys.andalus.harness.observability.event.EventSequence;
import tech.kayys.andalus.harness.observability.event.AndalusEvent;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Replay engine for reconstructing deterministic state representations from the event journal.
 */
public class EventReplay {

    private final EventJournal journal;

    public EventReplay(EventJournal journal) {
        this.journal = Objects.requireNonNull(journal, "journal");
    }

    public <S> ReplayResult<S> replay(
            ExecutionId executionId,
            S initialState,
            EventReducer<S> reducer,
            ReplayOptions options
    ) {
        Objects.requireNonNull(executionId, "executionId");
        Objects.requireNonNull(initialState, "initialState");
        Objects.requireNonNull(reducer, "reducer");
        options = options != null ? options : ReplayOptions.full();

        EventFilter filter = EventFilter.forExecution(executionId);
        Collection<AndalusEvent> events = journal.read(filter);

        List<AndalusEvent> sortedEvents = events.stream()
                .sorted((a, b) -> a.sequence().compareTo(b.sequence()))
                .toList();

        S current = initialState;
        int count = 0;
        EventSequence lastSeq = EventSequence.of(0L);

        for (AndalusEvent evt : sortedEvents) {
            if (options.upToSequence().isPresent() && evt.sequence().compareTo(options.upToSequence().get()) > 0) {
                break;
            }
            if (options.upToTimestamp().isPresent() && evt.timestamp().isAfter(options.upToTimestamp().get())) {
                break;
            }
            current = reducer.apply(current, evt);
            count++;
            lastSeq = evt.sequence();
        }

        return new ReplayResult<>(executionId, current, count, lastSeq);
    }
}
