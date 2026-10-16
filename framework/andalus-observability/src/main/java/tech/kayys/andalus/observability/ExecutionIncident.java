package tech.kayys.andalus.observability;

import java.time.Instant;
import java.util.Map;

public record ExecutionIncident(
        String id,
        String executionId,
        String traceId,
        Severity severity,
        String category,
        String message,
        Instant timestamp,
        Map<String, Object> attributes
) {

    public enum Severity {
        INFO,
        WARNING,
        ERROR,
        CRITICAL
    }

    public ExecutionIncident {
        timestamp = timestamp == null
                ? Instant.now()
                : timestamp;

        attributes = attributes == null
                ? Map.of()
                : Map.copyOf(attributes);
    }
}
