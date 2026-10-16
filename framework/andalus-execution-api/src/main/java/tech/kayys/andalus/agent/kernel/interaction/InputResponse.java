
package tech.kayys.andalus.agent.kernel.interaction;

import java.time.Instant;
import java.util.Map;

public record InputResponse(
        String requestId,
        String runId,
        Object value,
        String actor,
        Map<String, Object> metadata,
        Instant receivedAt
) {

    public InputResponse {
        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);

        receivedAt = receivedAt == null
                ? Instant.now()
                : receivedAt;
    }

    public static InputResponse answered(
            String requestId,
            String runId,
            Object value
    ) {
        return new InputResponse(
                requestId,
                runId,
                value,
                "human",
                Map.of(),
                Instant.now()
        );
    }
}
