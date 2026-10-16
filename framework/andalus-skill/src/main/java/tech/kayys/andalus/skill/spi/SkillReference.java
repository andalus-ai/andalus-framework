package tech.kayys.andalus.skill.spi;

import java.util.Objects;
import java.util.Optional;

public record SkillReference(
        String id,
        Optional<String> version
) {

    public SkillReference {
        Objects.requireNonNull(id, "id must not be null");

        if (id.isBlank()) {
            throw new IllegalArgumentException(
                    "id must not be blank"
            );
        }

        id = id.trim();

        version = version == null
                ? Optional.empty()
                : version;
    }

    public static SkillReference of(String id) {
        return new SkillReference(
                id,
                Optional.empty()
        );
    }

    public static SkillReference of(
            String id,
            String version) {

        return new SkillReference(
                id,
                Optional.ofNullable(version)
        );
    }
}
