package tech.kayys.wayang.harness.api;

import tech.kayys.wayang.harness.context.DefaultHarnessIdentity;
import tech.kayys.wayang.harness.context.DefaultHarnessSession;
import tech.kayys.wayang.harness.context.HarnessIdentity;
import tech.kayys.wayang.harness.context.HarnessSession;
import tech.kayys.wayang.harness.environment.CapabilityId;
import tech.kayys.wayang.harness.resource.ResourceRequest;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Comprehensive invocation request submitted to the Harness for execution.
 */
public record HarnessRequest(
        String prompt,
        String targetAgentId,
        HarnessIdentity identity,
        HarnessSession session,
        Set<CapabilityId> requiredCapabilities,
        Set<ResourceRequest> requiredResources,
        Map<String, Object> attributes
) {

    public HarnessRequest {
        targetAgentId = Objects.requireNonNull(targetAgentId, "targetAgentId");
        identity = identity == null ? DefaultHarnessIdentity.of(targetAgentId) : identity;
        session = session == null ? DefaultHarnessSession.createNew() : session;
        requiredCapabilities = requiredCapabilities == null ? Set.of() : Set.copyOf(requiredCapabilities);
        requiredResources = requiredResources == null ? Set.of() : Set.copyOf(requiredResources);
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public HarnessRequest(
            String prompt,
            String targetAgentId,
            HarnessIdentity identity,
            HarnessSession session,
            Set<CapabilityId> requiredCapabilities,
            Map<String, Object> attributes
    ) {
        this(prompt, targetAgentId, identity, session, requiredCapabilities, Set.of(), attributes);
    }

    public static HarnessRequest of(String targetAgentId, String prompt) {
        return new HarnessRequest(prompt, targetAgentId, null, null, Set.of(), Set.of(), Map.of());
    }

    public static HarnessRequest of(String targetAgentId, String prompt, Set<CapabilityId> requiredCapabilities) {
        return new HarnessRequest(prompt, targetAgentId, null, null, requiredCapabilities, Set.of(), Map.of());
    }

    public static HarnessRequest of(String targetAgentId, String prompt, Set<CapabilityId> requiredCapabilities, Set<ResourceRequest> requiredResources) {
        return new HarnessRequest(prompt, targetAgentId, null, null, requiredCapabilities, requiredResources, Map.of());
    }
}
