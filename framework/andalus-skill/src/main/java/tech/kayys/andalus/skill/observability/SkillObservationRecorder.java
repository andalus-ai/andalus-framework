package tech.kayys.andalus.skill.observability;

import tech.kayys.andalus.skill.spi.SkillResult;

/**
 * SPI for recording operational observability events around Skill execution.
 */
public interface SkillObservationRecorder {

    /**
     * Called immediately before a Skill execution starts.
     */
    void executionStarted(SkillObservationContext context);

    /**
     * Called when a Skill execution completes (either successfully or with a failure result).
     */
    void executionCompleted(SkillObservationContext context, SkillResult result);

    /**
     * Called when an unexpected throwable occurs during Skill execution.
     */
    void executionFailed(SkillObservationContext context, Throwable error);
}
