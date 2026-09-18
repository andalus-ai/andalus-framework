package tech.kayys.wayang.harness.spi;
import tech.kayys.wayang.agent.AgentResponse;

/**
 * Defines the contract for harness result operations in the Wayang framework.
 */

public interface HarnessResult {
    AgentResponse getAgentResponse();
}
