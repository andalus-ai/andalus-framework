package tech.kayys.andalus.skill.capability;

import tech.kayys.andalus.skill.spi.SkillManifest;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Bridge that converts {@link SkillManifest#capabilities()} strings into
 * typed {@link CapabilityRequirement} objects suitable for the existing
 * Andalus capability resolution system.
 */
public final class SkillCapabilityRequirements {

    private SkillCapabilityRequirements() {}

    /**
     * Convert manifest capability identifiers to typed requirements.
     * All capabilities declared in the manifest are treated as mandatory.
     *
     * @param manifest the Skill manifest; may be {@code null}
     * @return an immutable set of requirements; never {@code null}
     */
    public static Set<CapabilityRequirement> from(SkillManifest manifest) {
        if (manifest == null || manifest.capabilities() == null || manifest.capabilities().isEmpty()) {
            return Set.of();
        }
        return manifest.capabilities().stream()
                .filter(cap -> cap != null && !cap.isBlank())
                .map(CapabilityRequirement::required)
                .collect(Collectors.toUnmodifiableSet());
    }
}
