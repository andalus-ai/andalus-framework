package tech.kayys.wayang.spi.sandbox.observability;

import java.time.Instant;
import java.util.Map;

public record SandboxFailure(
        String sandboxId,
        String executionId,
        String providerId,
        Instant timestamp,
        String category,
        String reason,
        Map<String, Object> attributes
) {

    public SandboxFailure {
        if (sandboxId == null || sandboxId.isBlank()) {
            throw new IllegalArgumentException(
                    "sandboxId must not be blank");
        }

        if (executionId == null || executionId.isBlank()) {
            throw new IllegalArgumentException(
                    "executionId must not be blank");
        }

        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException(
                    "category must not be blank");
        }

        attributes = attributes == null
                ? Map.of()
                : Map.copyOf(attributes);
    }
}
