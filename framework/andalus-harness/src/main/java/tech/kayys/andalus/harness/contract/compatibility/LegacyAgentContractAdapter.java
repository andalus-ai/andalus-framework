package tech.kayys.andalus.harness.contract.compatibility;

import tech.kayys.andalus.spi.agent.AgentContract;
import tech.kayys.andalus.spi.agent.AgentContractVersion;
import tech.kayys.andalus.spi.agent.AgentDataContract;
import tech.kayys.andalus.spi.agent.AgentIdentity;
import tech.kayys.andalus.spi.agent.AgentLifecycleFeature;
import tech.kayys.andalus.spi.agent.AgentMetadata;
import tech.kayys.andalus.spi.agent.AgentProtocol;
import tech.kayys.andalus.spi.agent.AgentCapabilities;

import java.util.Set;

/**
 * Compatibility adapter from the original Harness AgentContract
 * to the canonical Andalus SPI AgentContract.
 */
public final class LegacyAgentContractAdapter
        implements AgentContract {

    private final tech.kayys.andalus.harness.contract.AgentContract delegate;

    public LegacyAgentContractAdapter(
            tech.kayys.andalus.harness.contract.AgentContract delegate) {

        if (delegate == null) {
            throw new IllegalArgumentException(
                    "delegate cannot be null");
        }

        this.delegate = delegate;
    }

    public tech.kayys.andalus.harness.contract.AgentContract delegate() {
        return delegate;
    }

    @Override
    public AgentIdentity identity() {
        return AgentIdentity.of(
                delegate.identity().agentId());
    }

    @Override
    public AgentContractVersion version() {
        var version = delegate.version();

        return AgentContractVersion.of(
                version.major(),
                version.minor(),
                0);
    }

    @Override
    public AgentCapabilities capabilities() {
        var capabilities = delegate.capabilities();

        return new AgentCapabilities(
                capabilities.required(),
                capabilities.optional(),
                capabilities.delegatable());
    }

    @Override
    public AgentDataContract input() {
        return AgentDataContract.any();
    }

    @Override
    public AgentDataContract output() {
        return AgentDataContract.any();
    }

    @Override
    public Set<AgentLifecycleFeature> lifecycle() {
        return Set.of(
                AgentLifecycleFeature.EXECUTE,
                AgentLifecycleFeature.CANCEL);
    }

    @Override
    public Set<AgentProtocol> protocols() {
        return Set.of(AgentProtocol.IN_PROCESS);
    }

    @Override
    public AgentMetadata metadata() {
        return AgentMetadata.empty();
    }
}
