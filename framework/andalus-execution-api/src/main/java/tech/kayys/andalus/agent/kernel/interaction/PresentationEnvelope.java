
package tech.kayys.andalus.agent.kernel.interaction;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public record PresentationEnvelope(
        String id,
        String runId,
        PresentationTarget target,
        Presentation presentation,
        Instant createdAt,
        Map<String, Object> metadata
) {

    public PresentationEnvelope {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(runId, "runId");
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(presentation, "presentation");

        createdAt = createdAt == null
                ? Instant.now()
                : createdAt;

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }
}
