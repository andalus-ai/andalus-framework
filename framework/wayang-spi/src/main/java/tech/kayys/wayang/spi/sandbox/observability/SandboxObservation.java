package tech.kayys.wayang.spi.sandbox.observability;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

public record SandboxObservation(
        String observationId,
        SandboxObservationType type,
        Instant timestamp,
        String sandboxId,
        String executionId,
        String tenantId,
        String agentId,
        String correlationId,
        String providerId,
        Map<String, Object> attributes
) {

    public SandboxObservation {
        if (observationId == null || observationId.isBlank()) {
            throw new IllegalArgumentException(
                    "observationId must not be blank");
        }

        if (type == null) {
            throw new IllegalArgumentException(
                    "type must not be null");
        }

        if (timestamp == null) {
            throw new IllegalArgumentException(
                    "timestamp must not be null");
        }

        if (sandboxId == null || sandboxId.isBlank()) {
            throw new IllegalArgumentException(
                    "sandboxId must not be blank");
        }

        attributes = attributes == null
                ? Map.of()
                : Map.copyOf(attributes);
    }

    public Optional<String> tenant() {
        return optional(tenantId);
    }

    public Optional<String> agent() {
        return optional(agentId);
    }

    public Optional<String> correlation() {
        return optional(correlationId);
    }

    private static Optional<String> optional(String value) {
        return value == null || value.isBlank()
                ? Optional.empty()
                : Optional.of(value);
    }
}
