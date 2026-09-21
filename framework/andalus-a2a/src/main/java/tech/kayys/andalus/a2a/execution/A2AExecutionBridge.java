package tech.kayys.andalus.a2a.execution;

import tech.kayys.andalus.communication.api.AgentResponse;

import java.util.concurrent.CompletionStage;

/**
 * Protocol/Execution Bridge between A2A transport and Andalus execution layer.
 */
public interface A2AExecutionBridge {

    CompletionStage<AgentResponse> execute(A2AExecutionRequest request);

    CompletionStage<A2ATaskExecution> submit(A2AExecutionRequest request);
}
