package tech.kayys.wayang.harness.observability.projection;

import tech.kayys.wayang.harness.execution.state.ExecutionId;
import tech.kayys.wayang.harness.observability.event.EventDomain;
import tech.kayys.wayang.harness.observability.event.WayangEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

/**
 * Compact high-level summary of an execution projected from its historical events.
 */
public record ExecutionSummary(
        ExecutionId executionId,
        int totalEvents,
        int turnCount,
        int toolCallCount,
        int modelCallCount,
        int artifactCount,
        Duration duration,
        String terminalStatus
) {
    public static class Projector implements EventProjection<ExecutionSummary> {

        private final ExecutionId executionId;

        public Projector(ExecutionId executionId) {
            this.executionId = executionId;
        }

        @Override
        public ExecutionSummary project(Collection<WayangEvent> events) {
            if (events == null || events.isEmpty()) {
                return new ExecutionSummary(executionId, 0, 0, 0, 0, 0, Duration.ZERO, "UNKNOWN");
            }

            List<WayangEvent> sorted = events.stream()
                    .sorted((a, b) -> a.sequence().compareTo(b.sequence()))
                    .toList();

            int turns = 0;
            int tools = 0;
            int models = 0;
            int artifacts = 0;
            String status = "UNKNOWN";

            Instant first = sorted.get(0).timestamp();
            Instant last = sorted.get(sorted.size() - 1).timestamp();

            for (WayangEvent evt : sorted) {
                if (evt.type().domain() == EventDomain.AGENT && evt.type().action().equalsIgnoreCase("turn_completed")) {
                    turns++;
                } else if (evt.type().domain() == EventDomain.TOOL && evt.type().action().equalsIgnoreCase("completed")) {
                    tools++;
                } else if (evt.type().domain() == EventDomain.MODEL && evt.type().action().equalsIgnoreCase("completed")) {
                    models++;
                } else if (evt.type().domain() == EventDomain.ARTIFACT && evt.type().action().equalsIgnoreCase("created")) {
                    artifacts++;
                } else if (evt.type().domain() == EventDomain.EXECUTION) {
                    status = evt.type().action().toUpperCase();
                }
            }

            Duration dur = Duration.between(first, last);
            return new ExecutionSummary(executionId, sorted.size(), turns, tools, models, artifacts, dur, status);
        }
    }
}
