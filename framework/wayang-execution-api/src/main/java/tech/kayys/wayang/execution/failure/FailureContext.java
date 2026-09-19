package tech.kayys.wayang.execution.failure;

import java.time.Instant;
import java.util.Map;

public record FailureContext(
        String workerId,
        String stepName,
        Instant occurredAt,
        Map<String, Object> attributes
) {
    public FailureContext {
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }
}
