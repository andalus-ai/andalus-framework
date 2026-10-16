package tech.kayys.andalus.skill.composition;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps inputs and preceding step results into inputs for the next step.
 */
public interface SkillDataMapper {

    /**
     * Map data for the next step.
     */
    SkillData map(
            SkillData initialInput,
            Map<String, SkillStepResult> previousSteps,
            SkillStep step
    );

    /**
     * Default mapper that accumulates all previous step outputs into the input map.
     */
    static SkillDataMapper defaultMapper() {
        return (initialInput, previousSteps, step) -> {
            Map<String, Object> merged = new HashMap<>(initialInput.values());
            if (previousSteps != null) {
                for (Map.Entry<String, SkillStepResult> entry : previousSteps.entrySet()) {
                    if (entry.getValue().success()) {
                        merged.putAll(entry.getValue().output().values());
                    }
                }
            }
            return SkillData.of(merged);
        };
    }
}
