package tech.kayys.wayang.knowledge.snapshot;

import java.util.List;
import java.util.Map;

/**
 * Represents a knowledge rule snapshot.
 *
 * <p>Its components capture `rules`, `aggregate fingerprint`, `metadata`.</p>
 *
 * @param rules the rules
 * @param aggregateFingerprint the aggregate fingerprint
 * @param metadata the metadata
 */


public record KnowledgeRuleSnapshot(
        List<KnowledgeVersionReference> rules,
        String aggregateFingerprint,
        Map<String, Object> metadata
) {

    public KnowledgeRuleSnapshot {
        rules = rules == null ? List.of() : List.copyOf(rules);
        aggregateFingerprint = aggregateFingerprint == null ? "" : aggregateFingerprint;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public List<KnowledgeVersionReference> references() {
        return rules;
    }
}
