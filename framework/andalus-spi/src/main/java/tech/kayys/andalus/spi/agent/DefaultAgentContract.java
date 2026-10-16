package tech.kayys.andalus.spi.agent;

import java.util.Objects;
import java.util.Set;

/**
 * Immutable default Agent Contract implementation.
 */
public record DefaultAgentContract(
        AgentIdentity identity,
        AgentContractVersion version,
        AgentCapabilities capabilities,
        AgentDataContract input,
        AgentDataContract output,
        Set<AgentLifecycleFeature> lifecycle,
        Set<AgentProtocol> protocols,
        AgentMetadata metadata
) implements AgentContract {

    public DefaultAgentContract {
        Objects.requireNonNull(
                identity,
                "identity cannot be null");

        Objects.requireNonNull(
                version,
                "version cannot be null");

        capabilities = capabilities == null
                ? AgentCapabilities.empty()
                : capabilities;

        input = input == null
                ? AgentDataContract.any()
                : input;

        output = output == null
                ? AgentDataContract.any()
                : output;

        lifecycle = lifecycle == null
                ? Set.of(AgentLifecycleFeature.EXECUTE)
                : Set.copyOf(lifecycle);

        protocols = protocols == null
                ? Set.of(AgentProtocol.IN_PROCESS)
                : Set.copyOf(protocols);

        metadata = metadata == null
                ? AgentMetadata.empty()
                : metadata;
    }

    public boolean supports(
            AgentLifecycleFeature feature) {

        return lifecycle.contains(feature);
    }

    public boolean supports(
            AgentProtocol protocol) {

        return protocols.contains(protocol);
    }
}
