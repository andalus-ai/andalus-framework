package tech.kayys.andalus.harness.spi;
import tech.kayys.andalus.agent.AgentContext;

/**
 * Defines the contract for harness context operations in the Andalus framework.
 */

public interface HarnessContext {
    AgentContext getAgentContext();
}
