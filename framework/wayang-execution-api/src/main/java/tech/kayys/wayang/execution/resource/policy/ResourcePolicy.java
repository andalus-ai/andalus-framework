package tech.kayys.wayang.execution.resource.policy;

import tech.kayys.wayang.execution.resource.model.VirtualResource;
import tech.kayys.wayang.execution.resource.provider.ResourceContext;

/**
 * Policy defining admission, constraints, and quotas for virtual execution resources.
 */
public interface ResourcePolicy {

    ResourcePolicyDecision evaluate(VirtualResource resource, ResourceContext context);
}
