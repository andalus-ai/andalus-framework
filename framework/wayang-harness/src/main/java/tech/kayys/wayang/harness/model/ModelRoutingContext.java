package tech.kayys.wayang.harness.model;

import tech.kayys.wayang.harness.context.HarnessIdentity;
import tech.kayys.wayang.harness.governance.policy.HarnessPolicy;
import tech.kayys.wayang.harness.resource.ResourceScope;

public record ModelRoutingContext(
        HarnessIdentity identity,
        ResourceScope resources,
        HarnessPolicy policy
) {
    public static ModelRoutingContext of(HarnessIdentity identity, ResourceScope resources) {
        return new ModelRoutingContext(identity, resources, null);
    }
}
