package tech.kayys.andalus.skill.markdown;

import tech.kayys.andalus.skill.spi.SkillContent;
import tech.kayys.andalus.skill.spi.SkillManifest;

import java.util.Objects;

/**
 * Parsed representation of a Skill package, consisting of its typed
 * {@link SkillManifest} and raw {@link SkillContent}.
 */
public record SkillPackage(
        SkillManifest manifest,
        SkillContent content
) {

    public SkillPackage {
        Objects.requireNonNull(manifest, "manifest must not be null");
        Objects.requireNonNull(content, "content must not be null");
    }
}
