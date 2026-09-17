package tech.kayys.wayang.execution.governance;

import tech.kayys.wayang.tool.ToolInvocation;

import java.util.Objects;
import java.util.Set;

/**
 * Authorization policy constraining which tools a specific agent is allowed to invoke.
 */
public final class AgentToolPolicy implements AuthorizationPolicy {

    private final String policyId;
    private final String agentId;
    private final Set<String> allowedTools;
    private final int priority;

    public AgentToolPolicy(String policyId, String agentId, Set<String> allowedTools, int priority) {
        if (policyId == null || policyId.isBlank()) {
            throw new IllegalArgumentException("policyId cannot be null or blank");
        }
        if (agentId == null || agentId.isBlank()) {
            throw new IllegalArgumentException("agentId cannot be null or blank");
        }
        this.policyId = policyId.trim();
        this.agentId = agentId.trim();
        this.allowedTools = allowedTools == null ? Set.of() : Set.copyOf(allowedTools);
        this.priority = priority;
    }

    public AgentToolPolicy(String policyId, String agentId, Set<String> allowedTools) {
        this(policyId, agentId, allowedTools, 100);
    }

    @Override
    public String id() {
        return policyId;
    }

    @Override
    public int priority() {
        return priority;
    }

    @Override
    public AuthorizationScope scope() {
        return AuthorizationScope.AGENT;
    }

    @Override
    public PolicyDecision evaluate(ToolInvocation invocation, ToolPermissionContext context) {
        return PolicyDecision.allow();
    }

    @Override
    public PolicyDecision evaluate(PolicyEvaluationContext context) {
        String callingAgent = context.agentId();
        if (callingAgent == null || !callingAgent.equals(agentId)) {
            // Not applicable to other agents
            return PolicyDecision.allow();
        }

        boolean allowed = allowedTools.isEmpty() || allowedTools.stream()
                .anyMatch(pattern -> ToolNameMatcher.matches(pattern, context.invocation().name()));

        if (!allowed) {
            return PolicyDecision.deny(
                    PolicyDecisionReason.TOOL_NOT_ALLOWED,
                    policyId,
                    "Agent " + agentId + " is not authorized to invoke tool: " + context.invocation().name()
            );
        }

        return PolicyDecision.allow(policyId, "Agent authorized for tool");
    }
}
