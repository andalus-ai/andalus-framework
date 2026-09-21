package tech.kayys.andalus.execution.core;

import tech.kayys.andalus.communication.api.AgentRequest;
import tech.kayys.andalus.execution.AgentInvocation;
import tech.kayys.andalus.execution.ExecutionContext;

import java.util.Objects;

/**
 * Default implementation of {@link AgentInvocation}.
 */
public record DefaultAgentInvocation(
        AgentRequest request,
        ExecutionContext context
) implements AgentInvocation {

    public DefaultAgentInvocation {
        Objects.requireNonNull(request, "request must not be null");
        Objects.requireNonNull(context, "context must not be null");
    }
}
