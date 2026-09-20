package tech.kayys.wayang.governance.adaptation;

import java.util.Optional;

/**
 * Interface for dynamically adapting capability routing based on failure observations.
 */
public interface DynamicPolicyAdapter {

    /**
     * Resolves the fallback capability for a failing capability, if failure count exceeds threshold.
     */
    Optional<String> resolveFallback(String failedCapability);

    /**
     * Records a failure occurrence for a capability.
     */
    void recordFailure(String capabilityUri);

    /**
     * Records a successful execution for a capability, resetting failure counters.
     */
    void recordSuccess(String capabilityUri);

    /**
     * Registers a fallback route for a capability.
     */
    void registerRoute(CapabilityFallbackRoute route);
}
