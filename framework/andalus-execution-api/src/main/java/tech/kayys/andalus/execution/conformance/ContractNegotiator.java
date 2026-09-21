package tech.kayys.andalus.execution.conformance;

import java.util.Set;

/**
 * Negotiates execution capability requirements against an environment's conformance profile.
 */
public interface ContractNegotiator {

    NegotiatedContract negotiate(Set<CapabilityRequirement> requirements, ConformanceProfile profile);
}
