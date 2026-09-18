package tech.kayys.wayang.harness.contract;

import tech.kayys.wayang.harness.contract.compatibility.CompatibilityContract;
import tech.kayys.wayang.harness.contract.lifecycle.LifecycleContract;
import tech.kayys.wayang.harness.contract.model.*;
import tech.kayys.wayang.harness.contract.protocol.ProtocolContract;
import tech.kayys.wayang.harness.contract.version.ContractVersion;
import tech.kayys.wayang.harness.protocol.AgentRef;

import java.util.Objects;

/**
 * Immutable reference implementation of AgentContract.
 */
public record DefaultAgentContract(
        AgentRef identity,
        AgentType type,
        ContractVersion version,
        AgentCapabilities capabilities,
        InputContract input,
        OutputContract output,
        ContextContract context,
        LifecycleContract lifecycle,
        ProtocolContract protocol,
        CompatibilityContract compatibility
) implements AgentContract {

    public DefaultAgentContract {
        Objects.requireNonNull(identity, "identity cannot be null");
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(version, "version cannot be null");
        capabilities = capabilities != null ? capabilities : AgentCapabilities.empty();
        context = context != null ? context : ContextContract.empty();
        lifecycle = lifecycle != null ? lifecycle : LifecycleContract.standard();
        protocol = protocol != null ? protocol : ProtocolContract.standard(version);
        compatibility = compatibility != null ? compatibility : CompatibilityContract.semanticMajorMatch(version);
    }
}
