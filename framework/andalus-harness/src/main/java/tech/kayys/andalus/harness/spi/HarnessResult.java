package tech.kayys.andalus.harness.spi;
import tech.kayys.andalus.agent.AgentResponse;

/**
 * Defines the contract for harness result operations in the Andalus framework.
 */

public interface HarnessResult {
    AgentResponse getAgentResponse();
}
