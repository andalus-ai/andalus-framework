package tech.kayys.andalus.spi.agent;

import java.util.Set;

/**
 * Stable, implementation-neutral contract describing an Andalus agent.
 *
 * <p>The contract describes what an agent is capable of and how it can be
 * invoked. It intentionally does not describe how the agent reasons or
 * executes internally.</p>
 *
 * <p>This is the foundation contract for local agents, remote agents,
 * plugins, marketplace agents, Wayang-created agents, A2A agents and
 * other Andalus integrations.</p>
 */
public interface AgentContract {

    /**
     * Stable agent identity.
     */
    AgentIdentity identity();

    /**
     * Contract version.
     */
    AgentContractVersion version();

    /**
     * Declared agent capabilities.
     */
    AgentCapabilities capabilities();

    /**
     * Input contract.
     */
    AgentDataContract input();

    /**
     * Output contract.
     */
    AgentDataContract output();

    /**
     * Supported lifecycle features.
     */
    Set<AgentLifecycleFeature> lifecycle();

    /**
     * Supported invocation protocols.
     */
    Set<AgentProtocol> protocols();

    /**
     * Optional descriptive metadata.
     */
    default AgentMetadata metadata() {
        return AgentMetadata.empty();
    }
}
