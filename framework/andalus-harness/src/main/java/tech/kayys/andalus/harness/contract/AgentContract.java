package tech.kayys.andalus.harness.contract;

import tech.kayys.andalus.harness.contract.compatibility.CompatibilityContract;
import tech.kayys.andalus.harness.contract.lifecycle.LifecycleContract;
import tech.kayys.andalus.harness.contract.model.*;
import tech.kayys.andalus.harness.contract.protocol.ProtocolContract;
import tech.kayys.andalus.harness.contract.version.ContractVersion;
import tech.kayys.andalus.harness.protocol.AgentRef;

/**
 * Stable, language-neutral ABI-like contract representing what an agent promises.
 * Andalus knows what the agent promises, not how the agent thinks internally.
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
