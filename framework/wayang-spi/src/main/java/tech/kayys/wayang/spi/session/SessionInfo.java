package tech.kayys.wayang.spi.session;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record SessionInfo(
        SessionId sessionId,
        String tenantId,
        String userId,
        String agentId,
        SessionState state,
        Instant createdAt,
        Instant lastActivityAt,
        Instant expiresAt,
        String correlationId,
        List<String> executionIds,
        Map<String, Object> attributes) {

    public SessionInfo {
        if (sessionId == null) {
            throw new IllegalArgumentException(
                    "sessionId must not be null");
        }

        executionIds = executionIds == null
                ? List.of()
                : List.copyOf(executionIds);

        attributes = attributes == null
                ? Map.of()
                : Map.copyOf(attributes);
    }

    public Optional<Instant> expires() {
        return Optional.ofNullable(expiresAt);
    }
}
