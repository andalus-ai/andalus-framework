package tech.kayys.andalus.skill.runtime;

import tech.kayys.andalus.skill.spi.Skill;
import tech.kayys.andalus.skill.spi.SkillResult;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

public final class DefaultSkillRuntime implements SkillRuntime {

    private final SkillResolver resolver;
    private final Executor executor;

    public DefaultSkillRuntime(
            SkillResolver resolver,
            Executor executor) {

        this.resolver = Objects.requireNonNull(resolver, "resolver must not be null");
        this.executor = Objects.requireNonNull(executor, "executor must not be null");
    }

    @Override
    public CompletionStage<SkillResult> execute(SkillExecutionRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        Instant startedAt = Instant.now();
        String invocationId = SkillInvocationIds.generate();

        return resolver
                .resolve(
                        new SkillResolutionRequest(
                                request.skill(),
                                request.attributes()
                        )
                )
                .thenComposeAsync(
                        resolved -> executeResolved(
                                resolved,
                                request,
                                invocationId,
                                startedAt
                        ),
                        executor
                );
    }

    private CompletionStage<SkillResult> executeResolved(
            Optional<Skill> resolved,
            SkillExecutionRequest request,
            String invocationId,
            Instant startedAt) {

        if (resolved.isEmpty()) {
            return CompletableFuture.completedFuture(
                    SkillResult.failure(
                            request.skill().id(),
                            "Skill not found: " + request.skill().id()
                    )
            );
        }

        Skill skill = resolved.get();

        String skillId = skill.descriptor() != null && skill.descriptor().id() != null
                ? skill.descriptor().id().asString()
                : request.skill().id();

        tech.kayys.andalus.skill.spi.SkillRuntimeConstraints constraints = new tech.kayys.andalus.skill.spi.SkillRuntimeConstraints(
                request.timeout().orElse(Duration.ZERO), 0, 0, 0, Map.of());

        DefaultSkillContext context = new DefaultSkillContext(
                request.inputs(),
                invocationId,
                request.executionId(),
                request.traceId(),
                request.turnId(),
                request.attributes(),
                constraints
        );

        return executeWithTimeout(
                skill,
                context,
                request.timeout(),
                skillId,
                invocationId,
                startedAt
        );
    }

    private CompletionStage<SkillResult> executeWithTimeout(
            Skill skill,
            DefaultSkillContext context,
            Optional<Duration> timeout,
            String skillId,
            String invocationId,
            Instant startedAt) {

        CompletionStage<SkillResult> execution = CompletableFuture.supplyAsync(
                () -> {
                    try {
                        SkillResult result = skill.execute(context);
                        return normalize(result, skillId, invocationId, startedAt);
                    } catch (Throwable error) {
                        return SkillResult.failure(skillId, error);
                    }
                },
                executor
        );

        if (timeout.isEmpty()) {
            return execution;
        }

        return execution.toCompletableFuture()
                .orTimeout(
                        timeout.get().toMillis(),
                        java.util.concurrent.TimeUnit.MILLISECONDS
                )
                .exceptionally(error -> SkillResult.failure(skillId, error));
    }

    private SkillResult normalize(
            SkillResult result,
            String skillId,
            String invocationId,
            Instant startedAt) {

        if (result == null) {
            return SkillResult.failure(
                    skillId,
                    "Skill returned null result"
            );
        }

        return result;
    }

    private static String attribute(
            SkillExecutionRequest request,
            String name) {

        Object value = request.attributes().get(name);
        return value == null ? "" : String.valueOf(value);
    }
}
