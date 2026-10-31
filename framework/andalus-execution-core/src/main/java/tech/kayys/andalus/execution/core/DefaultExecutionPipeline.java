package tech.kayys.andalus.execution.core;

import tech.kayys.andalus.execution.*;
import tech.kayys.andalus.execution.pipeline.ExecutionPipeline;

import java.util.Objects;
import java.util.concurrent.CompletionStage;

/**
 * Default {@link ExecutionPipeline} that runs authorized invocations through the
 * ordered interceptor chain and then the terminal handler.
 */
public final class DefaultExecutionPipeline implements ExecutionPipeline {

    private final InterceptorPipeline interceptorPipeline;
    private final AgentInvocationHandler terminalHandler;

    public DefaultExecutionPipeline(
            InterceptorPipeline interceptorPipeline,
            AgentInvocationHandler terminalHandler) {
        this.interceptorPipeline = Objects.requireNonNull(interceptorPipeline, "interceptorPipeline");
        this.terminalHandler = Objects.requireNonNull(terminalHandler, "terminalHandler");
    }

    @Override
    public CompletionStage<AgentResult> execute(AgentInvocation invocation) {
        Objects.requireNonNull(invocation, "invocation");

        ExecutionLifecycle lifecycle = invocation.context().lifecycle();
        if (lifecycle != ExecutionLifecycle.AUTHORIZED
                && lifecycle != ExecutionLifecycle.WAITING_OBLIGATION
                && lifecycle != ExecutionLifecycle.RESUMING) {
            throw new IllegalStateException(
                    "Execution must be authorized and ready to run before entering the pipeline; current state: "
                            + lifecycle);
        }

        invocation.context().transition(ExecutionLifecycle.RUNNING);

        DefaultAgentInvocationChain chain = new DefaultAgentInvocationChain(
                interceptorPipeline.sorted(),
                terminalHandler
        );

        return chain.proceed(invocation)
                .whenComplete((result, error) -> {
                    if (error != null) {
                        invocation.context().transition(ExecutionLifecycle.FAILED);
                    } else {
                        invocation.context().transition(ExecutionLifecycle.COMPLETED);
                    }
                });
    }
}
