package tech.kayys.andalus.a2a.handoff;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * A portable snapshot of memory items transferred during agent handoff.
 * Does NOT carry raw store references — always a value snapshot.
 */
public record HandoffMemorySnapshot(
        String sourceAgentId,
        String sessionId,
        List<HandoffMemoryItem> items,
        Instant snapshotAt
) {
    public HandoffMemorySnapshot {
        items = items == null ? List.of() : List.copyOf(items);
        snapshotAt = snapshotAt == null ? Instant.now() : snapshotAt;
    }

    public record HandoffMemoryItem(
            String key,
            String value,
            String type,
            double relevance,
            Map<String, Object> metadata
    ) {
        public HandoffMemoryItem {
            metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
        }
    }
}
