package tech.kayys.wayang.execution.conformance;

import java.util.Set;

/**
 * Resolver matching required capabilities against available registered capabilities.
 */
public interface CapabilityResolver {

    CapabilityResolution resolve(Set<CapabilityRequirement> requirements);
}
