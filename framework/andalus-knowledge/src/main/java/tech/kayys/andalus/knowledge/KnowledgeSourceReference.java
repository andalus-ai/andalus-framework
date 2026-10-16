package tech.kayys.andalus.knowledge;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public record KnowledgeSourceReference(
        String sourceId,
        String sourceType,
        Optional<String> version,
        Map<String, Object> configuration
) {
    public KnowledgeSourceReference {
        Objects.requireNonNull(sourceId, "sourceId must not be null");
        Objects.requireNonNull(sourceType, "sourceType must not be null");
        if (sourceId.isBlank()) throw new IllegalArgumentException("sourceId must not be blank");
        if (sourceType.isBlank()) throw new IllegalArgumentException("sourceType must not be blank");
        version = version == null ? Optional.empty() : version;
        configuration = configuration == null ? Map.of() : Map.copyOf(configuration);
    }
    public static KnowledgeSourceReference of(String sourceId, String sourceType) {
        return new KnowledgeSourceReference(sourceId, sourceType, Optional.empty(), Map.of());
    }
}
