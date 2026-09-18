package tech.kayys.wayang.spi.sandbox;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

public record SandboxContextRequest(
        String executionId,
        String tenantId,
        String userId,
        String agentId,
        String sessionId,
        String correlationId,
        Instant deadline,
        Map<String, Object> attributes
) {

    public SandboxContextRequest {
        if (executionId == null || executionId.isBlank()) {
            throw new IllegalArgumentException("executionId must not be blank");
        }

        attributes = attributes == null
                ? Map.of()
                : Map.copyOf(attributes);
    }

    public Optional<String> tenant() {
        return optional(tenantId);
    }

    public Optional<String> user() {
        return optional(userId);
    }

    public Optional<String> agent() {
        return optional(agentId);
    }

    public Optional<String> session() {
        return optional(sessionId);
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
