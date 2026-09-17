package tech.kayys.wayang.harness.context;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public record ContextLineage(
        String sourceId,
        String retrievalId,
        Optional<String> parentId,
        Instant timestamp
) {
    public ContextLineage {
        Objects.requireNonNull(sourceId, "sourceId");
        retrievalId = retrievalId == null ? "direct" : retrievalId;
        parentId = parentId == null ? Optional.empty() : parentId;
        timestamp = timestamp == null ? Instant.now() : timestamp;
    }
}
