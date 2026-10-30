package tech.kayys.andalus.execution.pipeline;

import tech.kayys.andalus.execution.AgentInvocation;
import tech.kayys.andalus.execution.AgentResult;

import java.util.concurrent.CompletionStage;

/**
 * Executes an authorized agent invocation through its interceptor chain and
 * terminal handler.
 *
 * <p>Authorization and any required external obligations must be completed
 * before an invocation enters this pipeline. The invocation lifecycle must be
 * {@code AUTHORIZED}, {@code WAITING_OBLIGATION} (after the obligation has been
 * satisfied), or {@code RESUMING}. The pipeline moves the invocation to
 * {@code RUNNING} before invoking interceptors or the terminal handler.
 *
 * <p>This is the single execution path regardless of protocol origin
 * (local, A2A, ANP, workflow).
 */
public interface ExecutionPipeline {

    /**
     * Executes an invocation that has passed authorization and is ready to run.
     *
     * @param invocation the authorized invocation to execute
     * @return a future that completes with the result or fails with an exception
     * @throws NullPointerException if invocation is null
     * @throws IllegalStateException if the invocation has not completed
     *         authorization or is not ready to run
     */
    CompletionStage<AgentResult> execute(AgentInvocation invocation);
}
