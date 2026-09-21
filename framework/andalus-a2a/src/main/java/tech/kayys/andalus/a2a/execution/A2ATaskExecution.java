package tech.kayys.andalus.a2a.execution;

import tech.kayys.andalus.communication.api.AgentResponse;
import tech.kayys.andalus.communication.task.TaskStatus;

import java.util.concurrent.CompletionStage;

/**
 * Handle representing an active asynchronous A2A task execution.
 */
public interface A2ATaskExecution {

    String a2aTaskId();

    String executionId();

    TaskStatus status();

    CompletionStage<AgentResponse> result();

    CompletionStage<Void> cancel();
}
