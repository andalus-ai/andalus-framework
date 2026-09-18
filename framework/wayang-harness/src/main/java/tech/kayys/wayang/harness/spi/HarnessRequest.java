package tech.kayys.wayang.harness.spi;
import tech.kayys.wayang.agent.AgentRequest;

/**
 * Defines the contract for harness request operations in the Wayang framework.
 */

public interface HarnessRequest {
    AgentRequest getAgentRequest();
    HarnessConfig getConfig();
}
