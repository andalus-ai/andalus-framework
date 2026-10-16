package tech.kayys.andalus.skill.runtime;

/**
 * Lightweight tracing record attached to skill resolution events.
 *
 * <p>Belongs in an observation/event payload rather than inside {@link
 * tech.kayys.andalus.skill.spi.Skill}, keeping the skill interface clean while
 * still providing the metadata needed to answer operational questions such as:
 * <ul>
 *   <li>Which provider supplied this skill?</li>
 *   <li>Which version was selected?</li>
 *   <li>How long did resolution take?</li>
 * </ul>
 */
public record SkillResolutionMetadata(
        String skillId,
        String requestedVersion,
        String resolvedVersion,
        String providerId,
        long durationMillis
) {
}
