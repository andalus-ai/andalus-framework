package tech.kayys.andalus.skill.composition;

import java.util.Objects;

/**
 * Result produced by a single step within a composite skill execution.
 */
public record SkillStepResult(
        String stepId,
        boolean success,
        SkillData output
) {

    public SkillStepResult {
        Objects.requireNonNull(stepId, "stepId must not be null");
        output = output == null ? SkillData.empty() : output;
    }

    public static SkillStepResult success(String stepId, SkillData output) {
        return new SkillStepResult(stepId, true, output);
    }

    public static SkillStepResult failure(String stepId) {
        return new SkillStepResult(stepId, false, SkillData.empty());
    }
}
