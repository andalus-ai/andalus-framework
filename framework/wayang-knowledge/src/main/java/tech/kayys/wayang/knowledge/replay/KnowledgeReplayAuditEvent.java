package tech.kayys.wayang.knowledge.replay;

import java.time.Instant;
import java.util.Map;

/**
 * Represents a knowledge replay audit event.
 *
 * <p>Its components capture `id`, `trace id`, `replay id`, `mode`, `status`, and other values.</p>
 *
 * @param id the id
 * @param traceId the trace id
 * @param replayId the replay id
 * @param mode the mode
 * @param status the status
 * @param createdAt the created at
 * @param metadata the metadata
 */


public record KnowledgeReplayAuditEvent(
        String id,
        String traceId,
        String replayId,
        KnowledgeReplayMode mode,
        KnowledgeReplayStatus status,
        Instant createdAt,
        Map<String, Object> metadata
) {

    public KnowledgeReplayAuditEvent {
        createdAt = createdAt == null ? Instant.now() : createdAt;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
