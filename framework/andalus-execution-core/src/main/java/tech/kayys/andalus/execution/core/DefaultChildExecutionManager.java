package tech.kayys.andalus.execution.core;

import tech.kayys.andalus.execution.AgentInvocation;
import tech.kayys.andalus.execution.AgentInvocationFactory;
import tech.kayys.andalus.execution.AgentResult;
import tech.kayys.andalus.execution.tree.ChildExecutionManager;
import tech.kayys.andalus.execution.tree.ChildExecutionRequest;
import tech.kayys.andalus.execution.tree.ChildExecutionResult;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

/**
 * Default delegated child execution manager.
 *
 * <p>The manager is intentionally independent of the concrete agent runtime.
 * The supplied executor decides how an invocation is actually executed.</p>
 */
public final class DefaultChildExecutionManager
        implements ChildExecutionManager {

    private final AgentInvocationFactory invocationFactory;
    private final Function<AgentInvocation, CompletionStage<AgentResult>> executor;

    public DefaultChildExecutionManager(
            AgentInvocationFactory invocationFactory,
            Function<AgentInvocation, CompletionStage<AgentResult>> executor) {

        this.invocationFactory = Objects.requireNonNull(
                invocationFactory,
                "invocationFactory must not be null");

        this.executor = Objects.requireNonNull(
                executor,
                "executor must not be null");
    }

    @Override
    public AgentInvocation create(
            AgentInvocation parent,
            ChildExecutionRequest request) {

        Objects.requireNonNull(parent, "parent must not be null");
        Objects.requireNonNull(request, "request must not be null");

        return invocationFactory.child(
                parent,
                request.request(),
                request.delegation()
        );
    }

    @Override
    public CompletionStage<ChildExecutionResult> execute(
            AgentInvocation parent,
            ChildExecutionRequest request) {

        AgentInvocation child = create(parent, request);

        String executionId =
                child.context().metadata().executionId();

        String parentExecutionId =
                child.context().metadata().parentExecutionId();

        String traceId =
                child.context().metadata().traceId();

        return executor.apply(child)
                .thenApply(result ->
                        new ChildExecutionResult(
                                executionId,
                                Objects.requireNonNull(
                                        parentExecutionId,
                                        "Child execution must have parentExecutionId"),
                                traceId,
                                result,
                                Instant.now(),
                                request.attributes()
                        )
                );
    }
}
