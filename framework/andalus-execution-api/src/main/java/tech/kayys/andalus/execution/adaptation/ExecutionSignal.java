package tech.kayys.andalus.execution.adaptation;

import tech.kayys.andalus.execution.mobility.ExecutionIdentity;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Normalised signal emitted by telemetry or runtime monitoring.
 */
public record ExecutionSignal(
    String signalId,
    SignalType type,
    SignalSeverity severity,
    SignalConfidence confidence,
    ExecutionIdentity executionId,
    Map<String, Object> payload,
    Instant emittedAt
) {
    public ExecutionSignal {
        Objects.requireNonNull(signalId, "signalId cannot be null");
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(severity, "severity cannot be null");
        Objects.requireNonNull(confidence, "confidence cannot be null");
        Objects.requireNonNull(executionId, "executionId cannot be null");
        payload = payload != null ? Map.copyOf(payload) : Map.of();
        Objects.requireNonNull(emittedAt, "emittedAt cannot be null");
    }

    public static ExecutionSignal of(String signalId, SignalType type, SignalSeverity severity, ExecutionIdentity executionId) {
        return new ExecutionSignal(signalId, type, severity, SignalConfidence.certain(), executionId, Map.of(), Instant.now());
    }

    public static ExecutionSignal of(String signalId, SignalType type, SignalSeverity severity, ExecutionIdentity executionId, Map<String, Object> payload) {
        return new ExecutionSignal(signalId, type, severity, SignalConfidence.certain(), executionId, payload, Instant.now());
    }
}
