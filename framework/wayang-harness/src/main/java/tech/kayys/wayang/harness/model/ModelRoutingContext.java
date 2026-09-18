package tech.kayys.wayang.harness.model;

import tech.kayys.wayang.harness.context.HarnessIdentity;
import tech.kayys.wayang.governance.policy.HarnessPolicy;
import tech.kayys.wayang.harness.resource.ResourceScope;

/**
 * Represents a model routing context.
 *
 * <p>Its components capture `identity`, `resources`, `policy`.</p>
 *
 * @param identity the identity
 * @param resources the resources
 * @param policy the policy
 */


public record ModelRoutingContext(
        HarnessIdentity identity,
        ResourceScope resources,
        HarnessPolicy policy
) {
    public static ModelRoutingContext of(HarnessIdentity identity, ResourceScope resources) {
        return new ModelRoutingContext(identity, resources, null);
    }
}
