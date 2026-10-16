package tech.kayys.andalus.harness.memory;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Logical declaration and configuration of an agent memory resource.
 */
public record MemoryDefinition(
        String id,
        String version,
        String name,
        String description,
        MemoryScope scope,
        List<MemoryType> types,
        MemoryStoreReference store,
        MemoryPolicy policy,
        List<String> capabilities,
        Map<String, Object> configuration,
        Map<String, Object> metadata
) {

    public MemoryDefinition {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(version, "version must not be null");
        name = name == null ? id : name;
        description = description == null ? "" : description;
        scope = scope == null ? MemoryScope.AGENT : scope;
        types = types == null ? List.of() : List.copyOf(types);
        policy = policy == null ? MemoryPolicy.permanent(scope) : policy;
        capabilities = capabilities == null ? List.of() : List.copyOf(capabilities);
        configuration = configuration == null ? Map.of() : Map.copyOf(configuration);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static MemoryDefinition of(String id, String version, MemoryStoreReference store) {
        return new MemoryDefinition(
                id,
                version,
                id,
                "",
                MemoryScope.AGENT,
                List.of(),
                store,
                MemoryPolicy.permanent(MemoryScope.AGENT),
                List.of(),
                Map.of(),
                Map.of()
        );
    }
}
