package tech.kayys.andalus.skill.analytics;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

/**
 * Filter query for querying skill analytics and operational statistics.
 */
public record SkillAnalyticsQuery(
        Optional<String> skillId,
        Optional<String> version,
        Optional<Instant> from,
        Optional<Instant> to,
        Optional<String> provider,
        Optional<String> tenant,
        Map<String, Object> dimensions
) {

    public SkillAnalyticsQuery {
        skillId    = skillId    == null ? Optional.empty() : skillId;
        version    = version    == null ? Optional.empty() : version;
        from       = from       == null ? Optional.empty() : from;
        to         = to         == null ? Optional.empty() : to;
        provider   = provider   == null ? Optional.empty() : provider;
        tenant     = tenant     == null ? Optional.empty() : tenant;
        dimensions = dimensions == null ? Map.of()         : Map.copyOf(dimensions);
    }

    public static SkillAnalyticsQuery forSkill(String skillId) {
        return new SkillAnalyticsQuery(
                Optional.ofNullable(skillId),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Map.of());
    }

    public static SkillAnalyticsQuery all() {
        return new SkillAnalyticsQuery(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Map.of());
    }
}
