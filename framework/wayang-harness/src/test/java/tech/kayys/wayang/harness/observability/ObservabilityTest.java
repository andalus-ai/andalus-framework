package tech.kayys.wayang.harness.observability;

import org.junit.jupiter.api.Test;
import tech.kayys.wayang.harness.execution.state.ExecutionId;
import tech.kayys.wayang.harness.observability.event.DefaultEventBus;
import tech.kayys.wayang.harness.observability.event.DefaultWayangEvent;
import tech.kayys.wayang.harness.observability.event.EventContext;
import tech.kayys.wayang.harness.observability.event.EventDomain;
import tech.kayys.wayang.harness.observability.event.EventFilter;
import tech.kayys.wayang.harness.observability.event.EventPayload;
import tech.kayys.wayang.harness.observability.event.EventSequence;
import tech.kayys.wayang.harness.observability.event.EventSubscription;
import tech.kayys.wayang.harness.observability.event.EventType;
import tech.kayys.wayang.harness.observability.event.InMemoryEventJournal;
import tech.kayys.wayang.harness.observability.event.WayangEvent;
import tech.kayys.wayang.harness.observability.metrics.InMemoryMetricsRuntime;
import tech.kayys.wayang.harness.observability.projection.ExecutionSummary;
import tech.kayys.wayang.harness.observability.projection.TimelineProjection;
import tech.kayys.wayang.harness.observability.replay.EventReplay;
import tech.kayys.wayang.harness.observability.replay.ReplayOptions;
import tech.kayys.wayang.harness.observability.replay.ReplayResult;
import tech.kayys.wayang.harness.observability.trace.ExecutionSpan;
import tech.kayys.wayang.harness.observability.trace.SpanKind;
import tech.kayys.wayang.harness.observability.trace.TraceId;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ObservabilityTest {

    @Test
    void testEventJournalAndPerExecutionSequencing() {
        InMemoryEventJournal journal = new InMemoryEventJournal();
        ExecutionId execId = ExecutionId.of("exec-obs-1");
        EventContext ctx = EventContext.ofExecution(execId);

        WayangEvent evt1 = DefaultWayangEvent.of(EventType.of(EventDomain.EXECUTION, "started"), ctx, EventPayload.empty());
        WayangEvent evt2 = DefaultWayangEvent.of(EventType.of(EventDomain.TOOL, "completed"), ctx, new EventPayload.ToolExecutionPayload("bash", true, "ok"));

        EventSequence seq1 = journal.append(evt1);
        EventSequence seq2 = journal.append(evt2);

        assertEquals(1L, seq1.value());
        assertEquals(2L, seq2.value());

        Collection<WayangEvent> read = journal.read(EventFilter.forExecution(execId));
        assertEquals(2, read.size());
    }

    @Test
    void testEventBusPubSub() {
        DefaultEventBus bus = new DefaultEventBus();
        List<WayangEvent> received = new ArrayList<>();

        try (EventSubscription sub = bus.subscribe(EventFilter.forDomain(EventDomain.TOOL), received::add)) {
            ExecutionId execId = ExecutionId.of("exec-obs-2");
            EventContext ctx = EventContext.ofExecution(execId);

            bus.publish(DefaultWayangEvent.of(EventType.of(EventDomain.TOOL, "completed"), ctx, EventPayload.empty()));
            bus.publish(DefaultWayangEvent.of(EventType.of(EventDomain.EXECUTION, "started"), ctx, EventPayload.empty()));

            assertEquals(1, received.size());
            assertEquals(EventDomain.TOOL, received.get(0).type().domain());
        }
    }

    @Test
    void testStateReplayAndProjections() {
        InMemoryEventJournal journal = new InMemoryEventJournal();
        ExecutionId execId = ExecutionId.of("exec-replay");
        EventContext ctx = EventContext.ofExecution(execId);

        journal.append(DefaultWayangEvent.of(EventType.of(EventDomain.EXECUTION, "started"), ctx, EventPayload.empty()));
        journal.append(DefaultWayangEvent.of(EventType.of(EventDomain.AGENT, "turn_completed"), ctx, EventPayload.empty()));
        journal.append(DefaultWayangEvent.of(EventType.of(EventDomain.TOOL, "completed"), ctx, EventPayload.empty()));
        journal.append(DefaultWayangEvent.of(EventType.of(EventDomain.ARTIFACT, "created"), ctx, EventPayload.empty()));
        journal.append(DefaultWayangEvent.of(EventType.of(EventDomain.EXECUTION, "completed"), ctx, EventPayload.empty()));

        // Test Replay
        EventReplay replay = new EventReplay(journal);
        ReplayResult<Integer> result = replay.replay(execId, 0, (count, event) -> count + 1, ReplayOptions.full());
        assertEquals(5, result.eventsReplayed());
        assertEquals(5, result.finalState());

        // Test ExecutionSummary Projection
        Collection<WayangEvent> all = journal.read(EventFilter.forExecution(execId));
        ExecutionSummary summary = new ExecutionSummary.Projector(execId).project(all);
        assertEquals(5, summary.totalEvents());
        assertEquals(1, summary.turnCount());
        assertEquals(1, summary.toolCallCount());
        assertEquals(1, summary.artifactCount());
        assertEquals("COMPLETED", summary.terminalStatus());

        // Test Timeline Projection
        TimelineProjection timeline = TimelineProjection.from(all);
        assertEquals(5, timeline.items().size());
        assertEquals("started", timeline.items().get(0).action());
    }

    @Test
    void testTraceSpanAndMetrics() {
        TraceId traceId = TraceId.generate();
        ExecutionSpan rootSpan = ExecutionSpan.start(traceId, Optional.empty(), "Turn-1", SpanKind.AGENT_TURN);
        assertNotNull(rootSpan.id());
        assertTrue(rootSpan.endedAt().isEmpty());

        ExecutionSpan ended = rootSpan.complete();
        assertTrue(ended.endedAt().isPresent());

        InMemoryMetricsRuntime metrics = new InMemoryMetricsRuntime();
        metrics.counter("tool.invocations").increment();
        metrics.counter("tool.invocations").increment();
        assertEquals(2L, metrics.counter("tool.invocations").count());

        metrics.timer("tool.latency").record(Duration.ofMillis(150));
        assertEquals(1L, metrics.timer("tool.latency").count());
        assertEquals(Duration.ofMillis(150), metrics.timer("tool.latency").totalTime());
    }
}
