package tech.kayys.andalus.skill.observability;

import tech.kayys.andalus.skill.spi.SkillResult;

/**
 * No-op implementation of {@link SkillObservationRecorder}.
 */
public final class NoopSkillObservationRecorder implements SkillObservationRecorder {

    public static final NoopSkillObservationRecorder INSTANCE = new NoopSkillObservationRecorder();

    @Override
    public void executionStarted(SkillObservationContext context) {
    }

    @Override
    public void executionCompleted(SkillObservationContext context, SkillResult result) {
    }

    @Override
    public void executionFailed(SkillObservationContext context, Throwable error) {
    }
}
