package tech.kayys.wayang.execution.fabric;

import tech.kayys.wayang.execution.conformance.ConformanceProfile;
import tech.kayys.wayang.execution.resource.requirement.ResourceRequirement;

import java.util.Set;

/**
 * Representation of a managed execution node within the distributed fabric.
 */
public interface WayangNode {

    NodeIdentity identity();

    NodeDescriptor descriptor();

    NodeStatus status();

    ConformanceProfile profile();

    Set<ResourceRequirement> availableResources();
}
