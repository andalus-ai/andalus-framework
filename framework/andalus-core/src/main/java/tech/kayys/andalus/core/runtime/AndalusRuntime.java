package tech.kayys.andalus.core.runtime;

import java.util.concurrent.CompletableFuture;

import tech.kayys.andalus.agent.AgentRequest;
import tech.kayys.andalus.agent.AgentResponse;
import tech.kayys.andalus.core.AgentDefinition;
import tech.kayys.andalus.extension.Extension;

/**
 * The top-level orchestrator contract for the Andalus Runtime.
 * This represents the environment in which agents execute, allowing the framework
 * to remain decoupled from the concrete implementation (e.g., generic vs edge).
 */
public interface AndalusRuntime extends Extension {
    
    /**
     * Executes an agent in the runtime context asynchronously.
     *
     * @param agent The definition of the agent to execute.
     * @param request The request containing inputs and metadata.
     * @return A CompletableFuture representing the execution response.
     */
    CompletableFuture<AgentResponse> executeAsync(AgentDefinition agent, AgentRequest request);
    
    /**
     * Checks if this runtime supports executing the given agent.
     *
     * @param agent The agent definition.
     * @return true if supported, false otherwise.
     */
    boolean supports(AgentDefinition agent);
}
