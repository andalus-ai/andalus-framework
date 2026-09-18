package tech.kayys.wayang.harness.spi;
import tech.kayys.wayang.agent.AgentContext;

/**
 * Defines the contract for harness context operations in the Wayang framework.
 */

public interface HarnessContext {
    AgentContext getAgentContext();
}
