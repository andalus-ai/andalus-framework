
package tech.kayys.andalus.agent.kernel.interaction;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public record InteractionRequest(
        String id,
        InteractionRequestType type,
        InteractionCorrelation correlation,
        String prompt,
        Map<String, Object> payload,
        InteractionRequestStatus status,
        Instant createdAt,
        Instant expiresAt
) {

    public InteractionRequest {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(correlation, "correlation");
        Objects.requireNonNull(prompt, "prompt");

        payload = payload == null
                ? Map.of()
                : Map.copyOf(payload);

        status = status == null
                ? InteractionRequestStatus.PENDING
                : status;

        createdAt = createdAt == null
                ? Instant.now()
                : createdAt;
    }

    public boolean pending() {
        return status == InteractionRequestStatus.PENDING;
    }

    public boolean expired() {
        return expiresAt != null
                && Instant.now().isAfter(expiresAt);
    }
}
