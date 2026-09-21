package tech.kayys.andalus.execution;

import tech.kayys.andalus.communication.api.AgentRequest;

/**
 * Represents a single agent invocation — pairing a request with its execution context.
 *
 * <p>This is the primary unit of work flowing through the {@link pipeline.ExecutionPipeline}.
 */
public interface AgentInvocation {

    /** The request to be processed. */
    AgentRequest request();

    /** The execution context for this invocation. */
    ExecutionContext context();
}
