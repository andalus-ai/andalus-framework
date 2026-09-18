package tech.kayys.wayang.spi.sandbox.observability;

import java.util.Map;
import java.util.Optional;

public record SandboxTraceContext(
        String traceId,
        String spanId,
        Map<String, String> baggage
) {

    public SandboxTraceContext {
        if (traceId == null || traceId.isBlank()) {
            throw new IllegalArgumentException(
                    "traceId must not be blank");
        }

        if (spanId == null || spanId.isBlank()) {
            throw new IllegalArgumentException(
                    "spanId must not be blank");
        }

        baggage = baggage == null
                ? Map.of()
                : Map.copyOf(baggage);
    }

    public Optional<String> baggage(
            String key) {

        return Optional.ofNullable(
                baggage.get(key));
    }
}
