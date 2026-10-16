package tech.kayys.andalus.skill.runtime;

import tech.kayys.andalus.skill.spi.Skill;

import java.util.List;
import java.util.Optional;

/**
 * Rich result of a skill resolution attempt.
 *
 * <p>A plain {@link Optional}{@code <Skill>} is sufficient for the basic
 * resolution path, but {@code SkillResolutionResult} enables observability:
 * callers can determine <em>why</em> resolution failed and which provider
 * supplied the skill, without resorting to exception inspection.</p>
 */
public record SkillResolutionResult(
        Optional<Skill> skill,
        ResolutionStatus status,
        Optional<String> providerId,
        List<String> diagnostics
) {

    public SkillResolutionResult {
        skill = skill == null
                ? Optional.empty()
                : skill;

        providerId = providerId == null
                ? Optional.empty()
                : providerId;

        diagnostics = diagnostics == null
                ? List.of()
                : List.copyOf(diagnostics);
    }

    /**
     * Returns {@code true} when resolution succeeded and a skill is present.
     */
    public boolean resolved() {
        return status == ResolutionStatus.RESOLVED
                && skill.isPresent();
    }

    // --- Factories ---

    public static SkillResolutionResult resolved(
            Skill skill,
            String providerId) {

        return new SkillResolutionResult(
                Optional.of(skill),
                ResolutionStatus.RESOLVED,
                Optional.of(providerId),
                List.of()
        );
    }

    public static SkillResolutionResult notFound(
            String skillId) {

        return new SkillResolutionResult(
                Optional.empty(),
                ResolutionStatus.NOT_FOUND,
                Optional.empty(),
                List.of("Skill not found: " + skillId)
        );
    }

    public static SkillResolutionResult error(
            String message) {

        return new SkillResolutionResult(
                Optional.empty(),
                ResolutionStatus.ERROR,
                Optional.empty(),
                List.of(message)
        );
    }
}
