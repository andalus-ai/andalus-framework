package tech.kayys.wayang.agent.orchestration.coordination;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * An immutable entry published onto the multi-agent semantic blackboard.
 */
public record BlackboardEntry(
    String entryId,
    String topic,
    String authorAgentId,
    String schemaUri,
    String payloadJson,
    long version,
    Instant publishedAt
) implements Serializable {

    public BlackboardEntry {
        Objects.requireNonNull(entryId, "entryId cannot be null");
        Objects.requireNonNull(topic, "topic cannot be null");
        Objects.requireNonNull(authorAgentId, "authorAgentId cannot be null");
        Objects.requireNonNull(payloadJson, "payloadJson cannot be null");
        Objects.requireNonNull(publishedAt, "publishedAt cannot be null");
    }

    public static BlackboardEntry of(String topic, String authorAgentId, String schemaUri, String payloadJson) {
        return new BlackboardEntry(
            "bb-" + UUID.randomUUID(),
            topic,
            authorAgentId,
            schemaUri != null ? schemaUri : "application/json",
            payloadJson,
            1L,
            Instant.now()
        );
    }

    public BlackboardEntry withPayload(String newPayloadJson) {
        return new BlackboardEntry(
            entryId,
            topic,
            authorAgentId,
            schemaUri,
            newPayloadJson,
            version + 1,
            Instant.now()
        );
    }
}
