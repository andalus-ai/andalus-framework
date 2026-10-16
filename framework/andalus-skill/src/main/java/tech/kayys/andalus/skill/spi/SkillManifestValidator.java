package tech.kayys.andalus.skill.spi;

import java.util.List;

public interface SkillManifestValidator {

    List<String> validate(SkillManifest manifest);

    default void validateOrThrow(SkillManifest manifest) {
        List<String> errors = validate(manifest);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("; ", errors));
        }
    }
}
