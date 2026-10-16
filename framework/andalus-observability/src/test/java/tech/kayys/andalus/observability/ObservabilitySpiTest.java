package tech.kayys.andalus.observability;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class ObservabilitySpiTest {

    @Test
    void testObservationEventDefaultsAndValidation() {
        ObservationEvent event = new ObservationEvent(
                null,
                "exec-123",
                "trace-456",
                ObservabilityEventType.EXECUTION_STARTED.name(),
                null,
                null
        );

        assertNotNull(event.id());
        assertTrue(event.id().startsWith("event-"));
        assertEquals("exec-123", event.executionId());
        assertEquals("trace-456", event.traceId());
        assertEquals(ObservabilityEventType.EXECUTION_STARTED.name(), event.type());
        assertNotNull(event.timestamp());
        assertTrue(event.attributes().isEmpty());

        assertThrows(NullPointerException.class, () ->
                new ObservationEvent("1", null, "trace-1", "type", null, null));
        assertThrows(NullPointerException.class, () ->
                new ObservationEvent("1", "exec-1", null, "type", null, null));
        assertThrows(NullPointerException.class, () ->
                new ObservationEvent("1", "exec-1", "trace-1", null, null, null));
    }

    @Test
    void testNoopObservabilityPublisher() {
        NoopObservabilityPublisher publisher = NoopObservabilityPublisher.INSTANCE;
        assertFalse(publisher.enabled());

        ObservationEvent event = new ObservationEvent(
                "e-1", "exec-1", "trace-1", "TURN_STARTED", Instant.now(), Map.of()
        );
        assertDoesNotThrow(() -> publisher.publish(event));
        assertDoesNotThrow(() -> publisher.publish("exec-1", "trace-1", ObservabilityEventType.TURN_STARTED));
    }

    @Test
    void testInMemoryObservabilitySink() {
        InMemoryObservabilitySink sink = new InMemoryObservabilitySink();

        sink.publish(new ObservationEvent("e1", "exec-1", "trace-1", ObservabilityEventType.EXECUTION_STARTED.name(), null, null));
        sink.publish(new ObservationEvent("e2", "exec-1", "trace-1", ObservabilityEventType.TURN_STARTED.name(), null, null));
        sink.publish(new ObservationEvent("e3", "exec-2", "trace-2", ObservabilityEventType.EXECUTION_STARTED.name(), null, null));

        assertEquals(3, sink.getEvents().size());
        assertEquals(2, sink.findByExecutionId("exec-1").size());
        assertEquals(1, sink.findByExecutionId("exec-2").size());
        assertEquals(0, sink.findByExecutionId("non-existent").size());
        assertEquals(2, sink.findByType(ObservabilityEventType.EXECUTION_STARTED.name()).size());
        assertEquals(1, sink.findByType(ObservabilityEventType.TURN_STARTED.name()).size());

        List<ObservationEvent> filtered = sink.filter(e -> "e2".equals(e.id()));
        assertEquals(1, filtered.size());
        assertEquals("e2", filtered.getFirst().id());

        sink.clear();
        assertTrue(sink.getEvents().isEmpty());
    }

    @Test
    void testDefaultObservabilityPublisher() {
        InMemoryObservabilitySink sink1 = new InMemoryObservabilitySink();
        InMemoryObservabilitySink sink2 = new InMemoryObservabilitySink();

        DefaultObservabilityPublisher publisher = new DefaultObservabilityPublisher(List.of(sink1));
        publisher.addSink(sink2);

        publisher.publish("exec-1", "trace-1", ObservabilityEventType.MODEL_STARTED);

        assertEquals(1, sink1.getEvents().size());
        assertEquals(1, sink2.getEvents().size());
        assertEquals(ObservabilityEventType.MODEL_STARTED.name(), sink1.getEvents().getFirst().type());

        // Test disabling
        publisher.setEnabled(false);
        assertFalse(publisher.enabled());
        publisher.publish("exec-1", "trace-1", ObservabilityEventType.MODEL_COMPLETED);

        assertEquals(1, sink1.getEvents().size()); // unchanged

        // Re-enable and test removing sink
        publisher.setEnabled(true);
        publisher.removeSink(sink2);
        publisher.publish("exec-1", "trace-1", ObservabilityEventType.MODEL_COMPLETED);

        assertEquals(2, sink1.getEvents().size());
        assertEquals(1, sink2.getEvents().size()); // unchanged
    }

    @Test
    void testSinkFailureDoesNotPropagate() {
        ObservabilitySink faultySink = new ObservabilitySink() {
            @Override
            public void publish(ObservationEvent event) {
                throw new RuntimeException("Sink database connection failed");
            }
        };

        InMemoryObservabilitySink goodSink = new InMemoryObservabilitySink();
        DefaultObservabilityPublisher publisher = new DefaultObservabilityPublisher(List.of(faultySink, goodSink));

        assertDoesNotThrow(() ->
                publisher.publish("exec-1", "trace-1", ObservabilityEventType.TOOL_STARTED)
        );

        // Good sink still received event
        assertEquals(1, goodSink.getEvents().size());
    }

    @Test
    void testExecutionMetricsAndUsageRecords() {
        TokenUsage tokens = new TokenUsage(100, 50, 20, 10);
        assertEquals(180, tokens.totalTokens());

        ResourceUsage resource = new ResourceUsage(150, 1024 * 1024, 512, 1024, 0, 0, null);
        assertEquals(150, resource.cpuTimeMillis());
        assertEquals(1024 * 1024, resource.memoryBytes());
        assertTrue(resource.additional().isEmpty());

        CostEstimate cost = new CostEstimate(BigDecimal.valueOf(0.045), null, null, null);
        assertEquals(BigDecimal.valueOf(0.045), cost.amount());
        assertEquals("USD", cost.currency());
        assertEquals("unknown", cost.basis());
        assertTrue(cost.breakdown().isEmpty());

        ExecutionMetrics metrics = new ExecutionMetrics(
                "exec-1",
                "trace-1",
                Duration.ofSeconds(5),
                2,
                4,
                3,
                1,
                0,
                100,
                50,
                150,
                0.045,
                null
        );

        assertEquals("exec-1", metrics.executionId());
        assertEquals(Duration.ofSeconds(5), metrics.duration());
        assertEquals(2, metrics.turns());
        assertEquals(4, metrics.modelCalls());
        assertEquals(3, metrics.toolCalls());
        assertTrue(metrics.resources().isEmpty());
    }

    @Test
    void testExecutionIncident() {
        ExecutionIncident incident = new ExecutionIncident(
                "inc-1",
                "exec-1",
                "trace-1",
                ExecutionIncident.Severity.ERROR,
                "TOOL_EXECUTION",
                "Tool timed out",
                null,
                Map.of("tool", "searchTool")
        );

        assertEquals("inc-1", incident.id());
        assertEquals(ExecutionIncident.Severity.ERROR, incident.severity());
        assertEquals("TOOL_EXECUTION", incident.category());
        assertEquals("Tool timed out", incident.message());
        assertNotNull(incident.timestamp());
        assertEquals("searchTool", incident.attributes().get("tool"));
    }

    @Test
    void testCompositeAgentExecutionObserverAndFaultTolerance() {
        AtomicBoolean obs1Called = new AtomicBoolean(false);
        AtomicBoolean obs2Called = new AtomicBoolean(false);

        AgentExecutionObserver obs1 = new AgentExecutionObserver() {
            @Override
            public void onEvent(ObservationEvent event) {
                obs1Called.set(true);
            }
        };

        AgentExecutionObserver failingObs = new AgentExecutionObserver() {
            @Override
            public void onEvent(ObservationEvent event) {
                throw new RuntimeException("Boom!");
            }
        };

        AgentExecutionObserver obs2 = new AgentExecutionObserver() {
            @Override
            public void onEvent(ObservationEvent event) {
                obs2Called.set(true);
            }
        };

        CompositeAgentExecutionObserver composite = new CompositeAgentExecutionObserver(
                List.of(obs1, failingObs, obs2)
        );

        assertEquals(3, composite.getObservers().size());

        ObservationEvent event = new ObservationEvent(
                "e1", "exec-1", "trace-1", ObservabilityEventType.TURN_STARTED.name(), null, null
        );

        assertDoesNotThrow(() -> composite.onEvent(event));
        assertTrue(obs1Called.get());
        assertTrue(obs2Called.get());
    }

    @Test
    void testPublisherAgentExecutionObserverBridge() {
        InMemoryObservabilitySink sink = new InMemoryObservabilitySink();
        DefaultObservabilityPublisher publisher = new DefaultObservabilityPublisher(List.of(sink));
        PublisherAgentExecutionObserver observer = new PublisherAgentExecutionObserver(publisher);

        // Test event
        observer.onEvent(new ObservationEvent("e1", "exec-1", "trace-1", ObservabilityEventType.EXECUTION_STARTED.name(), null, null));

        // Test token usage
        observer.onTokenUsage("exec-1", "trace-1", new TokenUsage(100, 50, 10, 5));

        // Test resource usage
        observer.onResourceUsage("exec-1", "trace-1", new ResourceUsage(200, 4096, 10, 20, 0, 0, null));

        // Test cost estimate
        observer.onCostEstimate("exec-1", "trace-1", new CostEstimate(BigDecimal.valueOf(0.015), "USD", "test", null));

        // Test incident
        observer.onIncident(new ExecutionIncident("inc-1", "exec-1", "trace-1", ExecutionIncident.Severity.WARNING, "TEST", "Test warning", null, null));

        List<ObservationEvent> events = sink.getEvents();
        assertEquals(5, events.size());

        assertEquals(ObservabilityEventType.EXECUTION_STARTED.name(), events.get(0).type());
        assertEquals(ObservabilityEventType.TOKEN_USAGE.name(), events.get(1).type());
        assertEquals(165L, events.get(1).attributes().get("totalTokens"));
        assertEquals(ObservabilityEventType.RESOURCE_USAGE.name(), events.get(2).type());
        assertEquals(200L, events.get(2).attributes().get("cpuTimeMillis"));
        assertEquals(ObservabilityEventType.COST_ESTIMATE.name(), events.get(3).type());
        assertEquals(BigDecimal.valueOf(0.015), events.get(3).attributes().get("amount"));
        assertEquals(ObservabilityEventType.INCIDENT.name(), events.get(4).type());
        assertEquals("WARNING", events.get(4).attributes().get("severity"));
    }
}
