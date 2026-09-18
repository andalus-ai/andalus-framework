package tech.kayys.wayang.harness.contract;

import tech.kayys.wayang.harness.contract.compatibility.CompatibilityContract;
import tech.kayys.wayang.harness.contract.lifecycle.LifecycleContract;
import tech.kayys.wayang.harness.contract.model.*;
import tech.kayys.wayang.harness.contract.protocol.ProtocolContract;
import tech.kayys.wayang.harness.contract.version.ContractVersion;
import tech.kayys.wayang.harness.protocol.AgentRef;

/**
 * Stable, language-neutral ABI-like contract representing what an agent promises.
 * Wayang knows what the agent promises, not how the agent thinks internally.
 */
public interface AgentContract {

    AgentRef identity();

    AgentType type();

    ContractVersion version();

    AgentCapabilities capabilities();

    InputContract input();

    OutputContract output();

    ContextContract context();

    LifecycleContract lifecycle();

    ProtocolContract protocol();

    CompatibilityContract compatibility();
}
