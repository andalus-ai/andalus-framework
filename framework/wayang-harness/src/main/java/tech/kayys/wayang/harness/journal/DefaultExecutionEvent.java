package tech.kayys.wayang.harness.journal;

import tech.kayys.wayang.harness.consistency.state.ExecutionId;
import tech.kayys.wayang.workflow.graph.NodeId;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Immutable reference record implementing {@link ExecutionEvent}.
 */
public record DefaultExecutionEvent(
        EventId id,
        ExecutionId executionId,
        EventSequence sequence,
        Instant timestamp,
        ExecutionEventType type,
        Optional<NodeId> nodeId,
        Map<String, Object> payload
) implements ExecutionEvent {

    public DefaultExecutionEvent {
        Objects.requireNonNull(id, "EventId cannot be null");
        Objects.requireNonNull(executionId, "ExecutionId cannot be null");
        Objects.requireNonNull(sequence, "EventSequence cannot be null");
        Objects.requireNonNull(type, "ExecutionEventType cannot be null");
        timestamp = timestamp != null ? timestamp : Instant.now();
        nodeId = nodeId != null ? nodeId : Optional.empty();
        payload = payload != null ? Map.copyOf(payload) : Map.of();
    }

    public static DefaultExecutionEvent of(
            ExecutionId executionId,
            EventSequence sequence,
            ExecutionEventType type
    ) {
        return new DefaultExecutionEvent(
                EventId.generate(),
                executionId,
                sequence,
                Instant.now(),
                type,
                Optional.empty(),
                Map.of()
        );
    }

    public static DefaultExecutionEvent ofNode(
            ExecutionId executionId,
            EventSequence sequence,
            ExecutionEventType type,
            NodeId nodeId
    ) {
        return new DefaultExecutionEvent(
                EventId.generate(),
                executionId,
                sequence,
                Instant.now(),
                type,
                Optional.ofNullable(nodeId),
                Map.of()
        );
    }

    public static DefaultExecutionEvent ofNode(
            ExecutionId executionId,
            EventSequence sequence,
            ExecutionEventType type,
            NodeId nodeId,
            Map<String, Object> payload
    ) {
        return new DefaultExecutionEvent(
                EventId.generate(),
                executionId,
                sequence,
                Instant.now(),
                type,
                Optional.ofNullable(nodeId),
                payload
        );
    }
}
