package tech.kayys.andalus.harness.decision.observability;

import java.util.List;
import java.util.Optional;

/**
 * SPI for instrumenting decision lifecycle events (Phase 4N).
 *
 * <p>Implementations forward records to the existing {@code EventBus} / {@code EventJournal}
 * and update the existing {@code MetricsRuntime} counters and timers.
 *
 * <p>Observability failures must not be confused with decision failures. All implementations
 * must follow the existing telemetry delivery guarantees; they must not block the decision
 * lifecycle on slow dashboards or unavailable exporters.
 */
public interface DecisionObservabilityRuntime {

    /**
     * Returns whether decision observability is currently enabled.
     */
    boolean enabled();

    /**
     * Records a decision lifecycle event. Implementations must be non-blocking and must not
     * propagate observability exceptions to the caller.
     */
    void record(DecisionObservabilityRecord event);

    /**
     * Queries recorded observability records for a specific decision trace.
     * Returns an empty list if observability is disabled or the trace is not found.
     */
    List<DecisionObservabilityRecord> queryByDecisionTrace(String decisionTraceId);

    /**
     * Queries recorded observability records for a given execution.
     */
    List<DecisionObservabilityRecord> queryByExecution(String executionId);

    /**
     * Returns the active payload capture mode.
     */
    DecisionPayloadCaptureMode captureMode();

    /**
     * Returns a no-op runtime that discards all records. Decision semantics are unaffected.
     */
    static DecisionObservabilityRuntime noop() {
        return new DecisionObservabilityRuntime() {
            @Override public boolean enabled() { return false; }
            @Override public void record(DecisionObservabilityRecord event) {}
            @Override public List<DecisionObservabilityRecord> queryByDecisionTrace(String id) { return List.of(); }
            @Override public List<DecisionObservabilityRecord> queryByExecution(String id) { return List.of(); }
            @Override public DecisionPayloadCaptureMode captureMode() { return DecisionPayloadCaptureMode.OFF; }
        };
    }
}
