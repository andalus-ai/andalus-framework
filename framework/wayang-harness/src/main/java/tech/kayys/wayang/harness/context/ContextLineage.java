package tech.kayys.wayang.harness.context;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a context lineage.
 *
 * <p>Its components capture `source id`, `retrieval id`, `parent id`, `timestamp`.</p>
 *
 * @param sourceId the source id
 * @param retrievalId the retrieval id
 * @param parentId the parent id
 * @param timestamp the timestamp
 */


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
