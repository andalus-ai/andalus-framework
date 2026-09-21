package tech.kayys.andalus.harness.spi;
import tech.kayys.andalus.agent.AgentRequest;

/**
 * Defines the contract for harness request operations in the Andalus framework.
 */

public interface HarnessRequest {
    AgentRequest getAgentRequest();
    HarnessConfig getConfig();
}
