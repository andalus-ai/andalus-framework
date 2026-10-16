package tech.kayys.andalus.execution;

import tech.kayys.andalus.agent.AgentResponse;

import java.util.concurrent.CompletionStage;

/**
 * Durable agent execution loop.
 *
 * <p>The loop coordinates turns for a single agent execution. It does not
 * implement model inference, tool execution, policy enforcement, or workflow
 * execution itself.</p>
 */
public interface AgentLoop {

    /**
     * Starts or resumes an execution.
     */
    CompletionStage<AgentResponse> run(AgentExecution execution);

    /**
     * Runs an execution synchronously.
     */
    default AgentResponse runSync(AgentExecution execution) {
        return run(execution).toCompletableFuture().join();
    }
}
