package tech.kayys.wayang.harness.information.knowledge;

import tech.kayys.wayang.harness.information.evidence.Evidence;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Result of querying a knowledge source.
 */
public record RetrievalResult(
        KnowledgeSourceId sourceId,
        String query,
        Collection<Evidence> items
) {
    public RetrievalResult {
        Objects.requireNonNull(sourceId, "sourceId");
        Objects.requireNonNull(query, "query");
        items = items != null ? List.copyOf(items) : List.of();
    }
}
