package tech.kayys.wayang.harness.tool;

import tech.kayys.wayang.harness.context.HarnessIdentity;
import tech.kayys.wayang.harness.governance.policy.HarnessPolicy;
import tech.kayys.wayang.harness.resource.ResourceScope;

/**
 * Represents a tool resolution context.
 *
 * <p>Its components capture `identity`, `resources`, `policy`.</p>
 *
 * @param identity the identity
 * @param resources the resources
 * @param policy the policy
 */


public record ToolResolutionContext(
        HarnessIdentity identity,
        ResourceScope resources,
        HarnessPolicy policy
) {
    public static ToolResolutionContext of(HarnessIdentity identity, ResourceScope resources) {
        return new ToolResolutionContext(identity, resources, null);
    }
}
