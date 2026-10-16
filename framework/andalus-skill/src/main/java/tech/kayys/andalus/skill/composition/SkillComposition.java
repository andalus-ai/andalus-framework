package tech.kayys.andalus.skill.composition;

import java.util.List;
import java.util.Map;

/**
 * Definition of a composite skill's steps and configuration.
 */
public record SkillComposition(
        List<SkillStep> steps,
        Map<String, Object> configuration
) {

    public SkillComposition {
        steps         = steps         == null ? List.of() : List.copyOf(steps);
        configuration = configuration == null ? Map.of()  : Map.copyOf(configuration);
    }

    public static SkillComposition of(List<SkillStep> steps) {
        return new SkillComposition(steps, Map.of());
    }
}
