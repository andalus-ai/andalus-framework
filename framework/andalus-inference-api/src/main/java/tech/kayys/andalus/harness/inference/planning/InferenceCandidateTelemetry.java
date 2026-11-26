package tech.kayys.andalus.harness.inference.planning;

import java.util.Map;
import java.util.Optional;

/**
 * Operational telemetry snapshot for an inference candidate (latency, cost, availability).
 */
public record InferenceCandidateTelemetry(
        Optional<Long> observedLatencyMillis,
        Optional<Double> errorRate,
        Optional<Long> estimatedCostPer1kTokensMicros,
        Map<String, Object> attributes
) {
    public InferenceCandidateTelemetry {
        observedLatencyMillis = observedLatencyMillis == null ? Optional.empty() : observedLatencyMillis;
        errorRate = errorRate == null ? Optional.empty() : errorRate;
        estimatedCostPer1kTokensMicros = estimatedCostPer1kTokensMicros == null ? Optional.empty() : estimatedCostPer1kTokensMicros;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static InferenceCandidateTelemetry empty() {
        return new InferenceCandidateTelemetry(Optional.empty(), Optional.empty(), Optional.empty(), Map.of());
    }

    public static InferenceCandidateTelemetry of(long latencyMillis, long costPer1kMicros) {
        return new InferenceCandidateTelemetry(
                Optional.of(latencyMillis),
                Optional.of(0.0),
                Optional.of(costPer1kMicros),
                Map.of()
        );
    }
}
