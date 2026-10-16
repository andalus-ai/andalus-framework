package tech.kayys.andalus.core;

import tech.kayys.andalus.spi.agent.AgentCapabilities;
import tech.kayys.andalus.spi.agent.AgentContract;
import tech.kayys.andalus.spi.agent.AgentContractVersion;
import tech.kayys.andalus.spi.agent.AgentDataContract;
import tech.kayys.andalus.spi.agent.AgentIdentity;
import tech.kayys.andalus.spi.agent.AgentLifecycleFeature;
import tech.kayys.andalus.spi.agent.AgentMetadata;
import tech.kayys.andalus.spi.agent.AgentProtocol;
import tech.kayys.andalus.spi.agent.DefaultAgentContract;

import java.util.Set;

/**
 * Projects an AgentDefinition into its public AgentContract.
 */
public final class AgentDefinitionContractFactory {

    private static final AgentContractVersion VERSION =
            AgentContractVersion.of(1, 0, 0);

    private AgentDefinitionContractFactory() {
    }

    public static AgentContract create(
            AgentDefinition definition) {

        if (definition == null) {
            throw new IllegalArgumentException(
                    "definition cannot be null");
        }

        return new DefaultAgentContract(
                definition.identity(),
                VERSION,
                new AgentCapabilities(
                        definition.capabilities(),
                        Set.of(),
                        Set.of()),
                AgentDataContract.any(),
                AgentDataContract.any(),
                Set.of(
                        AgentLifecycleFeature.INITIALIZE,
                        AgentLifecycleFeature.EXECUTE,
                        AgentLifecycleFeature.CANCEL),
                Set.of(
                        AgentProtocol.IN_PROCESS),
                AgentMetadata.of(
                        definition.role() != null ? definition.role() : "",
                        java.util.Map.of(
                                "agent.role",
                                definition.role() != null ? definition.role() : "")));
    }
}
