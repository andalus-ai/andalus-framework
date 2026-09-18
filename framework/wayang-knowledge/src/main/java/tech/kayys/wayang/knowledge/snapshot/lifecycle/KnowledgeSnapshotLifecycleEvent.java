package tech.kayys.wayang.knowledge.snapshot.lifecycle;

import tech.kayys.wayang.knowledge.snapshot.KnowledgeSnapshotId;

import java.time.Instant;
import java.util.Map;

/**
 * Represents a knowledge snapshot lifecycle event.
 *
 * <p>Its components capture `event id`, `snapshot id`, `previous state`, `new state`, `reason`, and other values.</p>
 *
 * @param eventId the event id
 * @param snapshotId the snapshot id
 * @param previousState the previous state
 * @param newState the new state
 * @param reason the reason
 * @param createdAt the created at
 * @param metadata the metadata
 */


public record KnowledgeSnapshotLifecycleEvent(
        String eventId,
        KnowledgeSnapshotId snapshotId,
        KnowledgeSnapshotLifecycleState previousState,
        KnowledgeSnapshotLifecycleState newState,
        String reason,
        Instant createdAt,
        Map<String, String> metadata
) {

    public KnowledgeSnapshotLifecycleEvent {
        createdAt = createdAt == null ? Instant.now() : createdAt;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
