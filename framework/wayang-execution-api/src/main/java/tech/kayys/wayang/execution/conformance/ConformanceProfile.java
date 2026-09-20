package tech.kayys.wayang.execution.conformance;

import java.util.Objects;
import java.util.Set;

/**
 * Declared conformance profile published by a runtime or agent harness component.
 */
public record ConformanceProfile(
    ProfileId id,
    Set<CapabilityDescriptor> capabilities,
    Set<ContractId> contracts,
    String environmentFingerprint
) {

    public ConformanceProfile {
        Objects.requireNonNull(id, "id cannot be null");
        capabilities = capabilities != null ? Set.copyOf(capabilities) : Set.of();
        contracts = contracts != null ? Set.copyOf(contracts) : Set.of();
    }

    public static ConformanceProfile of(ProfileId id, Set<CapabilityDescriptor> capabilities, Set<ContractId> contracts) {
        return new ConformanceProfile(id, capabilities, contracts, "default");
    }

    public boolean hasCapability(CapabilityId capabilityId) {
        return capabilities.stream().anyMatch(c -> c.id().equals(capabilityId));
    }

    public boolean supportsContract(ContractId contractId) {
        return contracts.contains(contractId);
    }
}
