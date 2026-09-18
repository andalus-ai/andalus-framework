package tech.kayys.wayang.harness.observability.replay;

import tech.kayys.wayang.harness.execution.state.ExecutionId;
import tech.kayys.wayang.harness.observability.event.EventSequence;

import java.util.Objects;

/**
 * Result of reconstructing state from an event journal replay.
 */
public record ReplayResult<S>(
        ExecutionId executionId,
        S finalState,
        int eventsReplayed,
        EventSequence lastSequence
) {
    public ReplayResult {
        Objects.requireNonNull(executionId, "executionId");
        Objects.requireNonNull(finalState, "finalState");
        Objects.requireNonNull(lastSequence, "lastSequence");
    }
}
