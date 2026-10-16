package tech.kayys.andalus.harness.decision.observability;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Structured observability record for a single decision lifecycle event (Phase 4N).
 *
 * <p>Carries correlation identifiers—executionId, turnId, traceId, decisionTraceId—so that
 * downstream consumers can reconstruct the full decision lineage without requiring full payloads.
 * By default only metadata fields are populated; payload fields are empty unless the configured
 * {@link DecisionPayloadCaptureMode} permits capture.
 *
 * <p>This record is published via the existing {@code EventBus} / {@code EventJournal} rather
 * than any new storage engine.
 */
public record DecisionObservabilityRecord(
        String id,
        String executionId,
        String turnId,
        String traceId,
        String decisionTraceId,
        String eventType,
        Instant occurredAt,
        Optional<String> strategyId,
        Optional<String> governanceOutcome,
        Optional<String> reasonCode,
        Optional<Integer> candidateCount,
        Optional<Long> durationMs,
        Map<String, Object> attributes
) {

    public DecisionObservabilityRecord {
        id = id == null || id.isBlank() ? "dobs-" + UUID.randomUUID() : id;
        Objects.requireNonNull(executionId, "executionId must not be null");
        Objects.requireNonNull(turnId, "turnId must not be null");
        Objects.requireNonNull(traceId, "traceId must not be null");
        Objects.requireNonNull(decisionTraceId, "decisionTraceId must not be null");
        Objects.requireNonNull(eventType, "eventType must not be null");
        occurredAt = occurredAt == null ? Instant.now() : occurredAt;
        strategyId = strategyId == null ? Optional.empty() : strategyId;
        governanceOutcome = governanceOutcome == null ? Optional.empty() : governanceOutcome;
        reasonCode = reasonCode == null ? Optional.empty() : reasonCode;
        candidateCount = candidateCount == null ? Optional.empty() : candidateCount;
        durationMs = durationMs == null ? Optional.empty() : durationMs;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    /**
     * Creates a minimal metadata-only record (METADATA capture mode).
     */
    public static DecisionObservabilityRecord metadata(
            String executionId,
            String turnId,
            String traceId,
            String decisionTraceId,
            String eventType) {

        return new DecisionObservabilityRecord(
                null,
                executionId,
                turnId,
                traceId,
                decisionTraceId,
                eventType,
                Instant.now(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Map.of()
        );
    }

    /**
     * Returns a copy of this record with the duration annotated.
     */
    public DecisionObservabilityRecord withDuration(long durationMs) {
        return new DecisionObservabilityRecord(
                id, executionId, turnId, traceId, decisionTraceId,
                eventType, occurredAt, strategyId, governanceOutcome,
                reasonCode, candidateCount, Optional.of(durationMs), attributes);
    }

    /**
     * Returns a copy of this record with additional attributes merged in.
     */
    public DecisionObservabilityRecord withAttributes(Map<String, Object> extra) {
        if (extra == null || extra.isEmpty()) return this;
        var merged = new java.util.LinkedHashMap<>(attributes);
        merged.putAll(extra);
        return new DecisionObservabilityRecord(
                id, executionId, turnId, traceId, decisionTraceId,
                eventType, occurredAt, strategyId, governanceOutcome,
                reasonCode, candidateCount, durationMs, merged);
    }
}
