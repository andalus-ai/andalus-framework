package tech.kayys.wayang.spi.sandbox.observability;

import java.time.Instant;
import java.util.Map;

public record SandboxHealth(
        String sandboxId,
        SandboxHealthStatus status,
        Instant checkedAt,
        String reason,
        Map<String, Object> attributes
) {

    public SandboxHealth {
        if (sandboxId == null || sandboxId.isBlank()) {
            throw new IllegalArgumentException(
                    "sandboxId must not be blank");
        }

        if (status == null) {
            throw new IllegalArgumentException(
                    "status must not be null");
        }

        if (checkedAt == null) {
            throw new IllegalArgumentException(
                    "checkedAt must not be null");
        }

        attributes = attributes == null
                ? Map.of()
                : Map.copyOf(attributes);
    }
}
