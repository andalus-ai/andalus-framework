package tech.kayys.wayang.harness.information.knowledge;

import java.util.Objects;
import java.util.UUID;

/**
 * Identifier for an external knowledge source (e.g. documentation, vector database, web search).
 */
public record KnowledgeSourceId(String value) {
    public KnowledgeSourceId {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("KnowledgeSourceId cannot be blank");
        }
    }

    public static KnowledgeSourceId of(String value) {
        return new KnowledgeSourceId(value);
    }

    public static KnowledgeSourceId generate() {
        return new KnowledgeSourceId("know-src-" + UUID.randomUUID());
    }
}
