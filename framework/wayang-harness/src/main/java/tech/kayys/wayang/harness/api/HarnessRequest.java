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
 *
 * @param prompt the instruction or task to execute
 * @param targetAgentId identifier of the agent that should handle the request
 * @param identity caller identity used for governance decisions
 * @param session session that owns the execution
 * @param requiredCapabilities capabilities that must be available
 * @param requiredResources resources that must be allocated
 * @param attributes additional request-scoped attributes
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

    /**
     * Creates a request with default identity, session, and empty constraints.
     *
     * @param targetAgentId identifier of the target agent
     * @param prompt instruction to execute
     * @return a normalized harness request
     */
    public static HarnessRequest of(String targetAgentId, String prompt) {
        return new HarnessRequest(prompt, targetAgentId, null, null, Set.of(), Set.of(), Map.of());
    }

    /**
     * Creates a request with required capability constraints.
     *
     * @param targetAgentId identifier of the target agent
     * @param prompt instruction to execute
     * @param requiredCapabilities capabilities that must be available
     * @return a normalized harness request
     */
    public static HarnessRequest of(String targetAgentId, String prompt, Set<CapabilityId> requiredCapabilities) {
        return new HarnessRequest(prompt, targetAgentId, null, null, requiredCapabilities, Set.of(), Map.of());
    }

    /**
     * Creates a request with capability and resource constraints.
     *
     * @param targetAgentId identifier of the target agent
     * @param prompt instruction to execute
     * @param requiredCapabilities capabilities that must be available
     * @param requiredResources resources that must be allocated
     * @return a normalized harness request
     */
    public static HarnessRequest of(String targetAgentId, String prompt, Set<CapabilityId> requiredCapabilities, Set<ResourceRequest> requiredResources) {
        return new HarnessRequest(prompt, targetAgentId, null, null, requiredCapabilities, requiredResources, Map.of());
    }
}
