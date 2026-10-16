package tech.kayys.andalus.knowledge;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record KnowledgeDefinition(
        String id,
        String version,
        String name,
        String description,
        KnowledgeSourceReference source,
        List<String> capabilities,
        Map<String, Object> configuration,
        Map<String, Object> metadata
) {
    public KnowledgeDefinition {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(version, "version must not be null");
        if (id.isBlank()) throw new IllegalArgumentException("id must not be blank");
        if (version.isBlank()) throw new IllegalArgumentException("version must not be blank");
        name = name == null ? id : name;
        description = description == null ? "" : description;
        capabilities = capabilities == null ? List.of() : List.copyOf(capabilities);
        configuration = configuration == null ? Map.of() : Map.copyOf(configuration);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static KnowledgeDefinition of(String id, String version, KnowledgeSourceReference source) {
        return new KnowledgeDefinition(id, version, id, "", source, List.of(), Map.of(), Map.of());
    }
}
