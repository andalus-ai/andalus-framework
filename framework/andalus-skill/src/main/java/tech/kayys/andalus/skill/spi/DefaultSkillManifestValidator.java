package tech.kayys.andalus.skill.spi;

import java.util.ArrayList;
import java.util.List;

public final class DefaultSkillManifestValidator implements SkillManifestValidator {

    @Override
    public List<String> validate(SkillManifest manifest) {
        if (manifest == null) {
            return List.of("manifest must not be null");
        }

        List<String> errors = new ArrayList<>();

        if (manifest.id().isBlank()) {
            errors.add("id must not be blank");
        }

        if (manifest.version().isBlank()) {
            errors.add("version must not be blank");
        }

        if (manifest.name().isBlank()) {
            errors.add("name must not be blank");
        }

        if (manifest.kind() == null) {
            errors.add("kind must not be null");
        }

        if (manifest.kind() == SkillKind.COMPOSITE
                && manifest.skills().isEmpty()
                && manifest.tools().isEmpty()) {
            errors.add("COMPOSITE skill must reference at least one skill or tool");
        }

        return List.copyOf(errors);
    }
}
