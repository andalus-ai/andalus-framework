package tech.kayys.andalus.harness.contract.registry;

import tech.kayys.andalus.harness.contract.AgentContract;
import tech.kayys.andalus.harness.contract.model.AgentType;
import tech.kayys.andalus.harness.contract.version.ContractVersion;
import tech.kayys.andalus.harness.protocol.AgentRef;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public interface AgentContractRegistry {

    void register(AgentContract contract);

    Optional<AgentContract> resolve(AgentRef agentRef);

    List<AgentContract> findCompatible(AgentType type, ContractVersion requiredVersion);

    static AgentContractRegistry inMemory() {
        return new InMemoryAgentContractRegistry();
    }
}

class InMemoryAgentContractRegistry implements AgentContractRegistry {
    private final Map<String, AgentContract> registry = new ConcurrentHashMap<>();

    @Override
    public void register(AgentContract contract) {
        if (contract != null) {
            registry.put(contract.identity().agentId(), contract);
        }
    }

    @Override
    public Optional<AgentContract> resolve(AgentRef agentRef) {
        if (agentRef == null) return Optional.empty();
        return Optional.ofNullable(registry.get(agentRef.agentId()));
    }

    @Override
    public List<AgentContract> findCompatible(AgentType type, ContractVersion requiredVersion) {
        return registry.values().stream()
                .filter(c -> c.type().equals(type))
                .filter(c -> c.version().isCompatibleWith(requiredVersion))
                .toList();
    }
}
