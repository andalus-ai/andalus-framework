package tech.kayys.wayang.harness.environment.v3.resource;

/**
 * Policy contract for authorizing and placing resource requests.
 */
public interface ResourcePolicy {

    ResourceDecision evaluate(
            ResourceRequest request,
            ResourceDescriptor resource,
            ResourcePolicyContext context
    );
}
