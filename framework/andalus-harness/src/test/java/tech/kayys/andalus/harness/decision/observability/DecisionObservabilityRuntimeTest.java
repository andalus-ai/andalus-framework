package tech.kayys.andalus.harness.decision.observability;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.harness.observability.event.AndalusEvent;
import tech.kayys.andalus.harness.observability.event.EventBus;
import tech.kayys.andalus.harness.observability.event.EventFilter;
import tech.kayys.andalus.harness.observability.event.EventHandler;
import tech.kayys.andalus.harness.observability.event.EventSubscription;
import tech.kayys.andalus.harness.observability.metrics.MetricsRuntime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Phase 4N — Decision Observability.
 */
class DecisionObservabilityRuntimeTest {

    // Minimal stub EventBus that captures published events in memory
    private static class CapturingEventBus implements EventBus {
        final List<AndalusEvent> captured = new ArrayList<>();
        @Override public void publish(AndalusEvent event) { captured.add(event); }
        @Override public EventSubscription subscribe(EventFilter f, EventHandler h) {
            return new EventSubscription() {
                @Override public boolean isActive() { return false; }
                @Override public void unsubscribe() {}
            };
        }
    }

    private CapturingEventBus eventBus;
    private MetricsRuntime metrics;
    private DefaultDecisionObservabilityRuntime runtime;

    @BeforeEach
    void setUp() {
        eventBus = new CapturingEventBus();
        metrics = MetricsRuntime.inMemory();
        runtime = new DefaultDecisionObservabilityRuntime(
                true, DecisionPayloadCaptureMode.METADATA, eventBus, metrics);
    }

    @Test
    void enabled_whenConstructedWithTrue() {
        assertThat(runtime.enabled()).isTrue();
    }

    @Test
    void noopRuntime_isDisabledAndRecordsNothing() {
        var noop = DecisionObservabilityRuntime.noop();
        assertThat(noop.enabled()).isFalse();
        assertThat(noop.captureMode()).isEqualTo(DecisionPayloadCaptureMode.OFF);
        noop.record(makeRecord("DECISION_STARTED"));
        assertThat(noop.queryByExecution("exec-1")).isEmpty();
    }

    @Test
    void record_publishesToEventBus() {
        runtime.record(makeRecord("DECISION_STARTED"));
        assertThat(eventBus.captured).hasSize(1);
        assertThat(eventBus.captured.get(0).type().canonicalName())
                .isEqualTo("andalus.decision.started");
    }

    @Test
    void record_incrementsStartedCounter() {
        runtime.record(makeRecord("DECISION_STARTED"));
        runtime.record(makeRecord("DECISION_STARTED"));
        assertThat(metrics.counter(DefaultDecisionObservabilityRuntime.METRIC_STARTED).count())
                .isEqualTo(2);
    }

    @Test
    void record_incrementsCompletedCounter() {
        runtime.record(makeRecord("DECISION_COMPLETED"));
        assertThat(metrics.counter(DefaultDecisionObservabilityRuntime.METRIC_COMPLETED).count())
                .isEqualTo(1);
    }

    @Test
    void record_incrementsGovernanceDenialCounter_onDenyOutcome() {
        var rec = makeRecord("DECISION_GOVERNANCE_COMPLETED");
        var withDeny = new DecisionObservabilityRecord(
                null, "exec-1", "turn-1", "trace-1", "dt-1",
                "DECISION_GOVERNANCE_COMPLETED", null,
                java.util.Optional.of("plan-and-execute"),
                java.util.Optional.of("DENY"),
                java.util.Optional.of("POLICY_VIOLATION"),
                java.util.Optional.empty(),
                java.util.Optional.empty(),
                Map.of());
        runtime.record(withDeny);
        assertThat(metrics.counter(DefaultDecisionObservabilityRuntime.METRIC_GOV_DENY).count())
                .isEqualTo(1);
    }

    @Test
    void record_doesNotIncrementDenialCounter_onAllowOutcome() {
        var withAllow = new DecisionObservabilityRecord(
                null, "exec-1", "turn-1", "trace-1", "dt-1",
                "DECISION_GOVERNANCE_COMPLETED", null,
                java.util.Optional.empty(),
                java.util.Optional.of("ALLOW"),
                java.util.Optional.empty(),
                java.util.Optional.empty(),
                java.util.Optional.empty(),
                Map.of());
        runtime.record(withAllow);
        assertThat(metrics.counter(DefaultDecisionObservabilityRuntime.METRIC_GOV_DENY).count())
                .isEqualTo(0);
    }

    @Test
    void queryByDecisionTrace_returnsOnlyMatchingRecords() {
        runtime.record(makeRecord("DECISION_STARTED"));
        // Record with different trace
        var other = new DecisionObservabilityRecord(
                null, "exec-2", "turn-2", "trace-2", "dt-OTHER",
                "DECISION_STARTED", null,
                java.util.Optional.empty(), java.util.Optional.empty(),
                java.util.Optional.empty(), java.util.Optional.empty(),
                java.util.Optional.empty(), Map.of());
        runtime.record(other);

        var result = runtime.queryByDecisionTrace("dt-1");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).decisionTraceId()).isEqualTo("dt-1");
    }

    @Test
    void queryByExecution_returnsAllEventsForExecution() {
        runtime.record(makeRecord("DECISION_STARTED"));
        runtime.record(makeRecord("DECISION_PRODUCED"));
        runtime.record(makeRecord("DECISION_COMPLETED"));

        var result = runtime.queryByExecution("exec-1");
        assertThat(result).hasSize(3);
    }

    @Test
    void record_whenDisabled_doesNotPublishOrStore() {
        var disabled = new DefaultDecisionObservabilityRuntime(
                false, DecisionPayloadCaptureMode.METADATA, eventBus, metrics);
        disabled.record(makeRecord("DECISION_STARTED"));
        assertThat(eventBus.captured).isEmpty();
        assertThat(disabled.queryByExecution("exec-1")).isEmpty();
    }

    @Test
    void record_swallowsExceptionsFromBus() {
        // A bus that always throws
        var throwingBus = new EventBus() {
            @Override public void publish(AndalusEvent e) { throw new RuntimeException("bus down"); }
            @Override public EventSubscription subscribe(EventFilter f, EventHandler h) {
                return new EventSubscription() {
                    @Override public boolean isActive() { return false; }
                    @Override public void unsubscribe() {}
                };
            }
        };
        var robustRuntime = new DefaultDecisionObservabilityRuntime(
                true, DecisionPayloadCaptureMode.METADATA, throwingBus, metrics);
        // Must not throw
        robustRuntime.record(makeRecord("DECISION_STARTED"));
        // Counter still 0 — exception happened before updateMetrics reached
        // (implementation catches early, so metrics may or may not increment — just verify no throw)
    }

    @Test
    void withDuration_annotatesRecord() {
        var rec = makeRecord("DECISION_COMPLETED").withDuration(150L);
        assertThat(rec.durationMs()).contains(150L);
    }

    @Test
    void captureMode_returnsConfiguredMode() {
        var fullRuntime = new DefaultDecisionObservabilityRuntime(
                true, DecisionPayloadCaptureMode.FULL, eventBus, metrics);
        assertThat(fullRuntime.captureMode()).isEqualTo(DecisionPayloadCaptureMode.FULL);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private DecisionObservabilityRecord makeRecord(String eventType) {
        return DecisionObservabilityRecord.metadata("exec-1", "turn-1", "trace-1", "dt-1", eventType);
    }
}
