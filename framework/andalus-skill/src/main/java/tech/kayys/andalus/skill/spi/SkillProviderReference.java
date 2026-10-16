package tech.kayys.andalus.skill.spi;

import java.util.Map;

public record SkillProviderReference(
        String type,
        String id,
        Map<String, Object> configuration
) {

    public SkillProviderReference {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException(
                    "provider type must not be blank");
        }

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                    "provider id must not be blank");
        }

        configuration = configuration == null
                ? Map.of()
                : Map.copyOf(configuration);
    }
}
