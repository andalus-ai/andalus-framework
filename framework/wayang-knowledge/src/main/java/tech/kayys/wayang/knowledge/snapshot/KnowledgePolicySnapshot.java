package tech.kayys.wayang.knowledge.snapshot;

import java.util.List;
import java.util.Map;

/**
 * Represents a knowledge policy snapshot.
 *
 * <p>Its components capture `policies`, `aggregate fingerprint`, `metadata`.</p>
 *
 * @param policies the policies
 * @param aggregateFingerprint the aggregate fingerprint
 * @param metadata the metadata
 */


public record KnowledgePolicySnapshot(
        List<KnowledgeVersionReference> policies,
        String aggregateFingerprint,
        Map<String, Object> metadata
) {

    public KnowledgePolicySnapshot {
        policies = policies == null ? List.of() : List.copyOf(policies);
        aggregateFingerprint = aggregateFingerprint == null ? "" : aggregateFingerprint;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public List<KnowledgeVersionReference> references() {
        return policies;
    }
}
