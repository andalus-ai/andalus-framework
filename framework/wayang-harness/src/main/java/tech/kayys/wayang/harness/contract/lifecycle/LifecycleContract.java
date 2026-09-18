package tech.kayys.wayang.harness.contract.lifecycle;

import java.util.Set;

/**
 * Contract declaring lifecycle features and draining policies supported by the agent.
 */
public record LifecycleContract(
        Set<LifecycleFeature> supportedFeatures,
        DrainPolicy drainPolicy
) {
    public LifecycleContract {
        supportedFeatures = supportedFeatures != null ? Set.copyOf(supportedFeatures) : Set.of();
    }

    public static LifecycleContract standard() {
        return new LifecycleContract(Set.of(LifecycleFeature.INITIALIZE, LifecycleFeature.DRAIN, LifecycleFeature.CANCEL), DrainPolicy.defaultPolicy());
    }

    public boolean supports(LifecycleFeature feature) {
        return supportedFeatures.contains(feature);
    }
}
