package tech.kayys.wayang.execution.portability;

import tech.kayys.wayang.execution.conformance.CapabilityRequirement;
import tech.kayys.wayang.execution.conformance.ContractId;
import tech.kayys.wayang.execution.mobility.ExecutionIdentity;
import tech.kayys.wayang.execution.mobility.state.StateReference;
import tech.kayys.wayang.execution.resource.requirement.ResourceRequirement;

import java.util.Objects;
import java.util.Set;

/**
 * Portable manifest declaring all requirements, contracts, state references, and metadata.
 */
public record ExecutionManifest(
    ExecutionIdentity executionId,
    Set<ContractId> contracts,
    Set<CapabilityRequirement> capabilities,
    Set<ResourceRequirement> resources,
    String effectPolicy,
    StateReference state,
    ProvenanceReference provenance,
    Replayability replayability,
    ArtifactManifest artifacts
) {
    public ExecutionManifest {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        contracts = contracts != null ? Set.copyOf(contracts) : Set.of();
        capabilities = capabilities != null ? Set.copyOf(capabilities) : Set.of();
        resources = resources != null ? Set.copyOf(resources) : Set.of();
        Objects.requireNonNull(effectPolicy, "effectPolicy cannot be null");
        Objects.requireNonNull(state, "state cannot be null");
        Objects.requireNonNull(provenance, "provenance cannot be null");
        Objects.requireNonNull(replayability, "replayability cannot be null");
        Objects.requireNonNull(artifacts, "artifacts cannot be null");
    }
}
