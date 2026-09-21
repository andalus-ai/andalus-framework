package tech.kayys.andalus.execution.fabric;

import tech.kayys.andalus.execution.conformance.CapabilityRequirement;
import tech.kayys.andalus.execution.mobility.ExecutionIdentity;
import tech.kayys.andalus.execution.resource.requirement.ResourceRequirement;

import java.util.Optional;
import java.util.Set;

/**
 * Placement engine matching execution requirements to optimal nodes across the fabric.
 */
public interface PlacementEngine {

    Optional<PlacementPlan> plan(
        ExecutionIdentity executionId,
        Set<CapabilityRequirement> capabilityRequirements,
        Set<ResourceRequirement> resourceRequirements,
        FabricSnapshot fabric
    );
}
