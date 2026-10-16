package tech.kayys.andalus.skill.composition;

import tech.kayys.andalus.skill.runtime.SkillExecutionRequest;
import tech.kayys.andalus.skill.runtime.SkillRuntime;
import tech.kayys.andalus.skill.spi.SkillContext;
import tech.kayys.andalus.skill.spi.SkillResult;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Sequential execution coordinator for composite skills.
 *
 * <p>Each step is executed in declared order. Child skills are executed
 * through {@link SkillRuntime}, preserving all governance, sandboxing, and
 * observability boundaries.</p>
 */
public final class DefaultCompositeSkillRuntime implements CompositeSkillRuntime {

    private final SkillRuntime skillRuntime;
    private final SkillDataMapper dataMapper;

    public DefaultCompositeSkillRuntime(SkillRuntime skillRuntime) {
        this(skillRuntime, SkillDataMapper.defaultMapper());
    }

    public DefaultCompositeSkillRuntime(SkillRuntime skillRuntime, SkillDataMapper dataMapper) {
        this.skillRuntime = Objects.requireNonNull(skillRuntime, "skillRuntime must not be null");
        this.dataMapper = Objects.requireNonNull(dataMapper, "dataMapper must not be null");
    }

    @Override
    public CompletionStage<SkillResult> execute(
            SkillComposition composition,
            SkillContext context) {

        Objects.requireNonNull(composition, "composition must not be null");
        Objects.requireNonNull(context, "context must not be null");

        SkillData initialInput = SkillData.of(context.getInputs());
        Map<String, SkillStepResult> stepResults = new LinkedHashMap<>();

        CompletableFuture<SkillResult> future = CompletableFuture.completedFuture(null);

        for (SkillStep step : composition.steps()) {
            future = future.thenCompose(prevResult -> {
                if (prevResult != null && !prevResult.isSuccess()) {
                    return CompletableFuture.completedFuture(prevResult);
                }

                SkillData stepInput = dataMapper.map(initialInput, stepResults, step);

                if (step.type() == SkillStepType.SKILL && step.skill().isPresent()) {
                    SkillExecutionRequest request = SkillExecutionRequest.of(
                            step.skill().get(),
                            stepInput.values(),
                            context.getExecutionId(),
                            context.getTraceId(),
                            context.getTurnId()
                    );

                    return skillRuntime.execute(request).thenApply(stepResult -> {
                        if (stepResult.isSuccess()) {
                            stepResults.put(step.id(), SkillStepResult.success(step.id(), SkillData.of(stepResult.getOutputs())));
                            return null; // continue to next step
                        } else {
                            stepResults.put(step.id(), SkillStepResult.failure(step.id()));
                            return stepResult; // break on failure
                        }
                    });
                } else {
                    // For non-skill or tool step where ToolRuntime isn't wired, pass through
                    stepResults.put(step.id(), SkillStepResult.success(step.id(), stepInput));
                    return CompletableFuture.completedFuture(null);
                }
            });
        }

        return future.thenApply(failedResult -> {
            if (failedResult != null && !failedResult.isSuccess()) {
                return failedResult;
            }

            // Consolidate all step outputs
            Map<String, Object> finalOutputs = new HashMap<>(context.getInputs());
            for (SkillStepResult stepRes : stepResults.values()) {
                if (stepRes.success()) {
                    finalOutputs.putAll(stepRes.output().values());
                }
            }
            return SkillResult.success(finalOutputs);
        });
    }
}
