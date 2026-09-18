package tech.kayys.wayang.harness.model;

import java.util.Map;

/**
 * Represents a model metadata.
 *
 * <p>Its components capture `family`, `architecture`, `local`, `attributes`.</p>
 *
 * @param family the family
 * @param architecture the architecture
 * @param local the local
 * @param attributes the attributes
 */


public record ModelMetadata(
        String family,
        String architecture,
        boolean local,
        Map<String, Object> attributes
) {
    public ModelMetadata {
        if (family == null) family = "unknown";
        if (architecture == null) architecture = "transformer";
        attributes = attributes != null ? Map.copyOf(attributes) : Map.of();
    }

    public static ModelMetadata local(String family) {
        return new ModelMetadata(family, "transformer", true, Map.of());
    }

    public static ModelMetadata cloud(String family) {
        return new ModelMetadata(family, "transformer", false, Map.of());
    }
}
