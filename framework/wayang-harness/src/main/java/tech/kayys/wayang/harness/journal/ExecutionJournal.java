package tech.kayys.wayang.harness.journal;

import tech.kayys.wayang.harness.consistency.state.ExecutionId;

import java.util.List;

/**
 * Append-only durable execution journal SPI.
 */
public interface ExecutionJournal {

    JournalId id();

    void append(ExecutionEvent event);

    List<ExecutionEvent> read(ExecutionId executionId, JournalPosition from);

    JournalPosition position(ExecutionId executionId);

    List<ExecutionEvent> history(ExecutionId executionId);
}
