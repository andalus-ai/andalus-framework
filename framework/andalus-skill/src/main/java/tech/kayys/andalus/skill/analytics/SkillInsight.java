package tech.kayys.andalus.skill.analytics;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Derived operational insight about a Skill, backed by evidence.
 */
public record SkillInsight(
        String type,
        String skillId,
        Optional<String> version,
        String severity,
        String title,
        String description,
        Map<String, Object> evidence,
        Instant generatedAt
) {

    public SkillInsight {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(skillId, "skillId");
        Objects.requireNonNull(severity, "severity");
        Objects.requireNonNull(title, "title");
        version     = version     == null ? Optional.empty() : version;
        description = description == null ? ""               : description;
        evidence    = evidence    == null ? Map.of()         : Map.copyOf(evidence);
        generatedAt = generatedAt == null ? Instant.now()    : generatedAt;
    }
}
