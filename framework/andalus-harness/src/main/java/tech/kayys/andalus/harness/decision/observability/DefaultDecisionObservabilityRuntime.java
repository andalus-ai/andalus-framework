package tech.kayys.andalus.harness.decision.observability;

import tech.kayys.andalus.harness.observability.event.DefaultAndalusEvent;
import tech.kayys.andalus.harness.observability.event.EventBus;
import tech.kayys.andalus.harness.observability.event.EventContext;
import tech.kayys.andalus.harness.observability.event.EventPayload;
import tech.kayys.andalus.harness.observability.event.EventType;
import tech.kayys.andalus.harness.observability.metrics.MetricsRuntime;
import tech.kayys.andalus.harness.execution.state.ExecutionId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Default in-memory {@link DecisionObservabilityRuntime} that forwards each decision lifecycle
 * event to the existing {@link EventBus} and increments the relevant {@link MetricsRuntime}
 * counters/timers (Phase 4N).
 *
 * <p><b>Architecture principles respected</b>
 * <ul>
 *   <li>No second event bus, metrics registry, or telemetry database is introduced.</li>
 *   <li>Record calls are best-effort; exceptions are caught and do not propagate to decision logic.</li>
 *   <li>METADATA is the default capture mode; full payloads are never captured unless explicitly configured.</li>
 * </ul>
 *
 * <p>The in-memory store is suitable for testing and community deployments. Pro/Enterprise
 * implementations can replace this via the {@link DecisionObservabilityRuntime} SPI without
 * changing any decision logic.
 */
public class DefaultDecisionObservabilityRuntime implements DecisionObservabilityRuntime {

    // --- Decision metric names (§4N.5) ---
    public static final String METRIC_STARTED   = "andalus_decision_started_total";
    public static final String METRIC_COMPLETED = "andalus_decision_completed_total";
    public static final String METRIC_FAILED    = "andalus_decision_failed_total";
    public static final String METRIC_DURATION  = "andalus_decision_duration_seconds";
    public static final String METRIC_GOV_DENY  = "andalus_decision_governance_denials_total";
    public static final String METRIC_WAITING   = "andalus_decision_waiting_total";
    public static final String METRIC_TIMEOUT   = "andalus_decision_timeouts_total";
    public static final String METRIC_REPLAN    = "andalus_decision_replanning_total";
    public static final String METRIC_CANDIDATE = "andalus_decision_candidate_count";
    public static final String METRIC_STRATEGY  = "andalus_decision_strategy_invocations_total";
    public static final String METRIC_VAL_FAIL  = "andalus_decision_validation_failures_total";

    private final boolean enabled;
    private final DecisionPayloadCaptureMode captureMode;
    private final EventBus eventBus;
    private final MetricsRuntime metrics;

    // In-memory store keyed by decisionTraceId and executionId for querying
    private final Map<String, CopyOnWriteArrayList<DecisionObservabilityRecord>> byDecisionTrace =
            new ConcurrentHashMap<>();
    private final Map<String, CopyOnWriteArrayList<DecisionObservabilityRecord>> byExecution =
            new ConcurrentHashMap<>();

    public DefaultDecisionObservabilityRuntime(
            boolean enabled,
            DecisionPayloadCaptureMode captureMode,
            EventBus eventBus,
            MetricsRuntime metrics) {

        this.enabled = enabled;
        this.captureMode = Objects.requireNonNullElse(captureMode, DecisionPayloadCaptureMode.METADATA);
        this.eventBus = Objects.requireNonNull(eventBus, "eventBus must not be null");
        this.metrics = Objects.requireNonNull(metrics, "metrics must not be null");
    }

    @Override
    public boolean enabled() {
        return enabled;
    }

    @Override
    public DecisionPayloadCaptureMode captureMode() {
        return captureMode;
    }

    @Override
    public void record(DecisionObservabilityRecord event) {
        if (!enabled || event == null) return;
        try {
            storeLocally(event);
            publishToEventBus(event);
            updateMetrics(event);
        } catch (Exception ex) {
            // Observability failure must not propagate to decision logic (§4N.8)
        }
    }

    @Override
    public List<DecisionObservabilityRecord> queryByDecisionTrace(String decisionTraceId) {
        if (decisionTraceId == null) return List.of();
        var list = byDecisionTrace.get(decisionTraceId);
        return list == null ? List.of() : new ArrayList<>(list);
    }

    @Override
    public List<DecisionObservabilityRecord> queryByExecution(String executionId) {
        if (executionId == null) return List.of();
        var list = byExecution.get(executionId);
        return list == null ? List.of() : new ArrayList<>(list);
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private void storeLocally(DecisionObservabilityRecord event) {
        byDecisionTrace
                .computeIfAbsent(event.decisionTraceId(), k -> new CopyOnWriteArrayList<>())
                .add(event);
        byExecution
                .computeIfAbsent(event.executionId(), k -> new CopyOnWriteArrayList<>())
                .add(event);
    }

    private void publishToEventBus(DecisionObservabilityRecord event) {
        // Build a minimal metadata payload — never full payloads unless FULL mode
        Map<String, Object> data = buildPayload(event);

        // Resolve the canonical EventType
        EventType type = resolveEventType(event.eventType());

        // Construct execution context (turnId and decisionTraceId go into attributes)
        ExecutionId execId = ExecutionId.of(event.executionId());
        EventContext ctx = EventContext.ofExecution(execId);

        DefaultAndalusEvent busEvent = DefaultAndalusEvent.of(
                type,
                ctx,
                EventPayload.MapPayload.of(data)
        );
        eventBus.publish(busEvent);
    }

    private Map<String, Object> buildPayload(DecisionObservabilityRecord event) {
        if (captureMode == DecisionPayloadCaptureMode.OFF) {
            return Map.of(
                    "eventType", event.eventType(),
                    "decisionTraceId", event.decisionTraceId()
            );
        }
        // METADATA / REDACTED / FULL — metadata fields only by default
        var map = new java.util.LinkedHashMap<String, Object>();
        map.put("eventType", event.eventType());
        map.put("executionId", event.executionId());
        map.put("turnId", event.turnId());
        map.put("traceId", event.traceId());
        map.put("decisionTraceId", event.decisionTraceId());
        map.put("occurredAt", event.occurredAt().toString());
        event.strategyId().ifPresent(v -> map.put("strategyId", v));
        event.governanceOutcome().ifPresent(v -> map.put("governanceOutcome", v));
        event.reasonCode().ifPresent(v -> map.put("reasonCode", v));
        event.candidateCount().ifPresent(v -> map.put("candidateCount", v));
        event.durationMs().ifPresent(v -> map.put("durationMs", v));
        // Full capture: also include custom attributes (only if permitted)
        if (captureMode == DecisionPayloadCaptureMode.FULL) {
            map.putAll(event.attributes());
        }
        return Map.copyOf(map);
    }

    private EventType resolveEventType(String eventTypeName) {
        // Attempt to match against known constants; fall back to a generic type
        return switch (eventTypeName) {
            case "DECISION_STARTED"              -> DecisionEventType.STARTED;
            case "DECISION_CONTEXT_READY"        -> DecisionEventType.CONTEXT_READY;
            case "DECISION_STRATEGY_SELECTED"    -> DecisionEventType.STRATEGY_SELECTED;
            case "DECISION_PLANNING_COMPLETED"   -> DecisionEventType.PLANNING_COMPLETED;
            case "DECISION_REASONING_COMPLETED"  -> DecisionEventType.REASONING_COMPLETED;
            case "DECISION_CANDIDATES_DISCOVERED"-> DecisionEventType.CANDIDATES_DISCOVERED;
            case "DECISION_CANDIDATE_EXCLUDED"   -> DecisionEventType.CANDIDATE_EXCLUDED;
            case "DECISION_PRODUCED"             -> DecisionEventType.PRODUCED;
            case "DECISION_VALIDATED"            -> DecisionEventType.VALIDATED;
            case "DECISION_GOVERNANCE_COMPLETED" -> DecisionEventType.GOVERNANCE_COMPLETED;
            case "DECISION_WAITING"              -> DecisionEventType.WAITING;
            case "DECISION_DISPATCHED"           -> DecisionEventType.DISPATCHED;
            case "DECISION_OUTCOME_RECEIVED"     -> DecisionEventType.OUTCOME_RECEIVED;
            case "DECISION_COMPLETED"            -> DecisionEventType.COMPLETED;
            case "DECISION_FAILED"               -> DecisionEventType.FAILED;
            case "DECISION_TIMED_OUT"            -> DecisionEventType.TIMED_OUT;
            case "DECISION_CANCELLED"            -> DecisionEventType.CANCELLED;
            case "DECISION_REPLANNING_REQUESTED" -> DecisionEventType.REPLANNING_REQUESTED;
            default -> EventType.of(
                    tech.kayys.andalus.harness.observability.event.EventDomain.DECISION,
                    eventTypeName.toLowerCase());
        };
    }

    private void updateMetrics(DecisionObservabilityRecord event) {
        switch (event.eventType()) {
            case "DECISION_STARTED"              -> metrics.counter(METRIC_STARTED).increment();
            case "DECISION_COMPLETED"            -> metrics.counter(METRIC_COMPLETED).increment();
            case "DECISION_FAILED"               -> metrics.counter(METRIC_FAILED).increment();
            case "DECISION_TIMED_OUT"            -> metrics.counter(METRIC_TIMEOUT).increment();
            case "DECISION_WAITING"              -> metrics.counter(METRIC_WAITING).increment();
            case "DECISION_REPLANNING_REQUESTED" -> metrics.counter(METRIC_REPLAN).increment();
            case "DECISION_STRATEGY_SELECTED"    -> metrics.counter(METRIC_STRATEGY).increment();
            case "DECISION_VALIDATED" -> {
                // Increment validation-failures only if the reasonCode indicates failure
                event.reasonCode().filter(r -> !r.isBlank())
                        .ifPresent(r -> metrics.counter(METRIC_VAL_FAIL).increment());
            }
            case "DECISION_GOVERNANCE_COMPLETED" -> {
                event.governanceOutcome().filter("DENY"::equals)
                        .ifPresent(o -> metrics.counter(METRIC_GOV_DENY).increment());
            }
            case "DECISION_CANDIDATES_DISCOVERED" -> {
                event.candidateCount().ifPresent(n ->
                        metrics.gauge(METRIC_CANDIDATE).set(n.doubleValue()));
            }
            default -> { /* other event types do not map to a dedicated counter */ }
        }
        // Record duration if present
        event.durationMs().ifPresent(ms ->
                metrics.timer(METRIC_DURATION).record(java.time.Duration.ofMillis(ms)));
    }
}
