package tech.kayys.wayang.state.provenance;

import java.util.Map;
import java.util.Objects;

public record ProvenanceOperation(String name, String category, Map<String, Object> attributes) {
    public ProvenanceOperation {
        Objects.requireNonNull(name, "name cannot be null");
        category = category == null ? "general" : category;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static ProvenanceOperation of(String name) {
        return new ProvenanceOperation(name, "general", Map.of());
    }

    public static ProvenanceOperation of(String name, String category) {
        return new ProvenanceOperation(name, category, Map.of());
    }
}
