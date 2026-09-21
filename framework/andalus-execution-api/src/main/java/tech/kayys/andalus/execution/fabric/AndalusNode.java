package tech.kayys.andalus.execution.fabric;

import tech.kayys.andalus.execution.conformance.ConformanceProfile;
import tech.kayys.andalus.execution.resource.requirement.ResourceRequirement;

import java.util.Set;

/**
 * Representation of a managed execution node within the distributed fabric.
 */
public interface AndalusNode {

    NodeIdentity identity();

    NodeDescriptor descriptor();

    NodeStatus status();

    ConformanceProfile profile();

    Set<ResourceRequirement> availableResources();
}
