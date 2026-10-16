package tech.kayys.andalus.skill.composition;

import tech.kayys.andalus.skill.spi.SkillReference;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A single step in a composite skill execution definition.
 */
public record SkillStep(
        String id,
        SkillStepType type,
        Optional<SkillReference> skill,
        Optional<String> tool,
        Map<String, Object> configuration
) {

    public SkillStep {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(type, "type must not be null");

        skill         = skill         == null ? Optional.empty() : skill;
        tool          = tool          == null ? Optional.empty() : tool;
        configuration = configuration == null ? Map.of()         : Map.copyOf(configuration);

        if (type == SkillStepType.SKILL && skill.isEmpty()) {
            throw new IllegalArgumentException("SKILL step requires a skill reference");
        }

        if (type == SkillStepType.TOOL && tool.isEmpty()) {
            throw new IllegalArgumentException("TOOL step requires a tool reference");
        }
    }

    public static SkillStep ofSkill(String id, SkillReference skillRef) {
        return new SkillStep(id, SkillStepType.SKILL, Optional.of(skillRef), Optional.empty(), Map.of());
    }

    public static SkillStep ofTool(String id, String toolName) {
        return new SkillStep(id, SkillStepType.TOOL, Optional.empty(), Optional.of(toolName), Map.of());
    }
}
