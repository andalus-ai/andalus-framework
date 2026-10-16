package tech.kayys.andalus.skill.observability;

import tech.kayys.andalus.skill.runtime.SkillExecutionRequest;
import tech.kayys.andalus.skill.runtime.SkillInvocationIds;
import tech.kayys.andalus.skill.runtime.SkillRuntime;
import tech.kayys.andalus.skill.spi.SkillResult;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.CompletionStage;

/**
 * Decorating {@link SkillRuntime} that wraps an underlying runtime and emits
 * operational telemetry to a {@link SkillObservationRecorder}.
 *
 * <p>Observability failures are trapped to guarantee they never cause the
 * core Skill execution to fail.</p>
 */
public final class ObservedSkillRuntime implements SkillRuntime {

    private final SkillRuntime delegate;
    private final SkillObservationRecorder recorder;

    public ObservedSkillRuntime(SkillRuntime delegate, SkillObservationRecorder recorder) {
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null");
        this.recorder = Objects.requireNonNull(recorder, "recorder must not be null");
    }

    @Override
    public CompletionStage<SkillResult> execute(SkillExecutionRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        String invocationId = SkillInvocationIds.generate();
        Instant startedAt = Instant.now();

        SkillObservationContext obsContext = new SkillObservationContext(
                invocationId,
                request.executionId(),
                request.traceId(),
                request.turnId(),
                request.skill().id(),
                request.skill().version().orElse(""),
                startedAt
        );

        try {
            recorder.executionStarted(obsContext);
        } catch (Throwable t) {
            // Observability failure must not break execution
        }

        return delegate.execute(request)
                .whenComplete((result, error) -> {
                    try {
                        if (error != null) {
                            recorder.executionFailed(obsContext, error);
                        } else {
                            recorder.executionCompleted(obsContext, result);
                        }
                    } catch (Throwable t) {
                        // Observability failure must not break execution
                    }
                });
    }

    public SkillRuntime delegate() {
        return delegate;
    }

    public SkillObservationRecorder recorder() {
        return recorder;
    }
}
