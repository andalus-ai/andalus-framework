package tech.kayys.wayang.spi.diagnostics;

import java.time.Duration;
import java.util.Map;

public record DiagnosticContext(
        String tenantId,
        String userId,
        String correlationId,
        Duration timeout,
        Map<String, Object> attributes) {

    public DiagnosticContext {
        attributes = attributes == null
                ? Map.of()
                : Map.copyOf(attributes);

        if (timeout == null) {
            timeout = Duration.ofSeconds(5);
        }

        if (timeout.isNegative() || timeout.isZero()) {
            throw new IllegalArgumentException(
                    "timeout must be positive");
        }
    }

    public static DiagnosticContext defaultContext() {
        return new DiagnosticContext(null, null, null, Duration.ofSeconds(5), Map.of());
    }
}
