package tech.kayys.andalus.harness.environment.v3.resource;

import tech.kayys.andalus.harness.context.HarnessIdentity;
import tech.kayys.andalus.harness.context.HarnessSession;

import java.util.Map;
import java.util.Objects;

/**
 * Context for evaluating resource governance policies.
 */
public record ResourcePolicyContext(
        HarnessIdentity identity,
        HarnessSession session,
        Map<String, Object> attributes
) {
    public ResourcePolicyContext {
        attributes = attributes != null ? Map.copyOf(attributes) : Map.of();
    }

    public static ResourcePolicyContext of(HarnessIdentity identity, HarnessSession session) {
        return new ResourcePolicyContext(identity, session, Map.of());
    }
}
