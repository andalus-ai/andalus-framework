package tech.kayys.andalus.harness.contract.compatibility;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.harness.contract.AgentContract;
import tech.kayys.andalus.harness.contract.DefaultAgentContract;
import tech.kayys.andalus.harness.contract.lifecycle.DrainPolicy;
import tech.kayys.andalus.harness.contract.lifecycle.LifecycleContract;
import tech.kayys.andalus.harness.contract.lifecycle.LifecycleFeature;
import tech.kayys.andalus.harness.contract.model.*;
import tech.kayys.andalus.harness.contract.protocol.*;
import tech.kayys.andalus.harness.contract.schema.SchemaRef;
import tech.kayys.andalus.harness.contract.version.ContractVersion;
import tech.kayys.andalus.harness.protocol.AgentRef;
import tech.kayys.andalus.spi.agent.AgentLifecycleFeature;
import tech.kayys.andalus.spi.agent.AgentProtocol;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LegacyAgentContractAdapterTest {

    @Test
    void adaptsLegacyHarnessContract() {
        AgentContract legacyContract = new DefaultAgentContract(
                AgentRef.of("legacy-agent"),
                AgentType.of("test-type"),
                ContractVersion.of(1, 2),
                new AgentCapabilities(Set.of("search"), Set.of("browser"), Set.of("delegate")),
                new InputContract(Set.of("task"), SchemaRef.of("input-schema", "1.0"), Set.of()),
                new OutputContract(SchemaRef.of("output-schema", "1.0"), Set.of(), Set.of()),
                new ContextContract(Set.of(), Set.of()),
                new LifecycleContract(Set.of(LifecycleFeature.DRAIN), DrainPolicy.defaultPolicy()),
                new ProtocolContract(
                        ContractVersion.of(1, 2),
                        Set.of(TransportType.IN_PROCESS),
                        Set.of(ProtocolFeature.STREAMING)
                ),
                CompatibilityContract.semanticMajorMatch(ContractVersion.of(1, 2))
        );

        LegacyAgentContractAdapter adapter = new LegacyAgentContractAdapter(legacyContract);

        assertThat(adapter.identity().id()).isEqualTo("legacy-agent");
        assertThat(adapter.version().major()).isEqualTo(1);
        assertThat(adapter.version().minor()).isEqualTo(2);
        assertThat(adapter.capabilities().requires("search")).isTrue();
        assertThat(adapter.capabilities().supportsOptional("browser")).isTrue();
        assertThat(adapter.capabilities().canDelegate("delegate")).isTrue();
        assertThat(adapter.lifecycle()).contains(AgentLifecycleFeature.EXECUTE, AgentLifecycleFeature.CANCEL);
        assertThat(adapter.protocols()).contains(AgentProtocol.IN_PROCESS);
    }
}
