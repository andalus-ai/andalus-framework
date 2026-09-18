package tech.kayys.wayang.harness.journal;

import tech.kayys.wayang.harness.consistency.state.ExecutionId;
import tech.kayys.wayang.harness.workflow.NodeId;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

/**
 * Immutable historical fact recorded in the execution journal.
 */
public interface ExecutionEvent {

    EventId id();

    ExecutionId executionId();

    EventSequence sequence();

    Instant timestamp();

    ExecutionEventType type();

    Optional<NodeId> nodeId();

    Map<String, Object> payload();
}
