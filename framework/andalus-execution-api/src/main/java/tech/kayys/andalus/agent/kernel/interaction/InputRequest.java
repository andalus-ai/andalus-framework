
package tech.kayys.andalus.agent.kernel.interaction;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public record InputRequest(
        String id,
        String runId,
        String prompt,
        String inputType,
        Map<String, Object> schema,
        boolean required,
        Instant createdAt,
        Instant expiresAt
) {

    public InputRequest {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(runId, "runId");
        Objects.requireNonNull(prompt, "prompt");

        inputType = inputType == null
                ? "text"
                : inputType;

        schema = schema == null
                ? Map.of()
                : Map.copyOf(schema);

        createdAt = createdAt == null
                ? Instant.now()
                : createdAt;
    }

    public InputRequest(
            String id,
            String runId,
            String prompt,
            String inputType,
            Map<String, Object> schema,
            boolean required
    ) {
        this(
                id,
                runId,
                prompt,
                inputType,
                schema,
                required,
                Instant.now(),
                null
        );
    }

    public boolean expired() {
        return expiresAt != null
                && Instant.now().isAfter(expiresAt);
    }
}
