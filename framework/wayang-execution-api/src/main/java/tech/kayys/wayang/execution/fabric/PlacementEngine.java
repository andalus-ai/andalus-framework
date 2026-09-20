package tech.kayys.wayang.execution.fabric;

import tech.kayys.wayang.execution.conformance.CapabilityRequirement;
import tech.kayys.wayang.execution.mobility.ExecutionIdentity;
import tech.kayys.wayang.execution.resource.requirement.ResourceRequirement;

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
