package tech.kayys.andalus.spi.agent;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AgentContractTest {

    @Test
    void createsMinimalContract() {

        var contract = new DefaultAgentContract(
                AgentIdentity.of("example-agent"),
                AgentContractVersion.of(1, 0, 0),
                AgentCapabilities.empty(),
                AgentDataContract.any(),
                AgentDataContract.any(),
                Set.of(AgentLifecycleFeature.EXECUTE),
                Set.of(AgentProtocol.IN_PROCESS),
                AgentMetadata.empty()
        );

        assertThat(contract.identity().id())
                .isEqualTo("example-agent");

        assertThat(contract.version())
                .isEqualTo(
                        AgentContractVersion.of(1, 0, 0));

        assertThat(contract.supports(
                AgentLifecycleFeature.EXECUTE))
                .isTrue();

        assertThat(contract.supports(
                AgentLifecycleFeature.PAUSE))
                .isFalse();
    }

    @Test
    void validatesContract() {

        var contract = new DefaultAgentContract(
                AgentIdentity.of("research-agent"),
                AgentContractVersion.of(1, 0, 0),
                new AgentCapabilities(
                        Set.of("research"),
                        Set.of("browser"),
                        Set.of("data-analysis")),
                AgentDataContract.any(),
                AgentDataContract.any(),
                Set.of(
                        AgentLifecycleFeature.EXECUTE,
                        AgentLifecycleFeature.PAUSE,
                        AgentLifecycleFeature.RESUME,
                        AgentLifecycleFeature.CANCEL),
                Set.of(
                        AgentProtocol.IN_PROCESS,
                        AgentProtocol.GRPC),
                AgentMetadata.of(
                        "Generic research agent",
                        Set.of())
        );

        var result =
                AgentContractValidator.validate(contract);

        assertThat(result.valid()).isTrue();
        assertThat(result.errors()).isEmpty();
    }
}
