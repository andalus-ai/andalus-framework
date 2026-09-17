package tech.kayys.wayang.execution.governance;

import tech.kayys.wayang.tool.ToolInvocation;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Standard rule-based {@link ToolPolicy} evaluating declarative {@link ToolPolicyRule}s.
 */
public final class DefaultToolPolicy implements IdentifiedToolPolicy {

    private final String id;
    private final int priority;
    private final boolean enabled;
    private final PolicyDefaultEffect defaultEffect;
    private final List<ToolPolicyRule> rules;

    public DefaultToolPolicy(String id, int priority, PolicyDefaultEffect defaultEffect, List<ToolPolicyRule> rules) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id cannot be null or blank");
        }
        this.id = id.trim();
        this.priority = priority;
        this.enabled = true;
        this.defaultEffect = defaultEffect != null ? defaultEffect : PolicyDefaultEffect.ALLOW;
        this.rules = rules == null ? List.of() : List.copyOf(rules);
    }

    public DefaultToolPolicy(List<ToolPolicyRule> rules) {
        this("default-tool-policy", 100, PolicyDefaultEffect.ALLOW, rules);
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public int priority() {
        return priority;
    }

    @Override
    public boolean enabled() {
        return enabled;
    }

    public List<ToolPolicyRule> rules() {
        return rules;
    }

    @Override
    public PolicyDecision evaluate(ToolInvocation invocation, ToolPermissionContext context) {
        return evaluate(PolicyEvaluationContexts.create(context, invocation));
    }

    @Override
    public PolicyDecision evaluate(PolicyEvaluationContext context) {
        Objects.requireNonNull(context, "context cannot be null");

        if (!enabled) {
            return PolicyDecision.allow();
        }

        for (ToolPolicyRule rule : rules) {
            if (matches(rule, context)) {
                return decision(rule);
            }
        }

        return defaultEffect == PolicyDefaultEffect.ALLOW
                ? PolicyDecision.allow()
                : PolicyDecision.deny(PolicyDecisionReason.NO_MATCHING_POLICY, id, "No matching policy rule found");
    }

    private boolean matches(ToolPolicyRule rule, PolicyEvaluationContext context) {
        if (!matchesTool(rule, context.invocation())) {
            return false;
        }

        if (!matchesTenant(rule, context)) {
            return false;
        }

        if (!matchesUser(rule, context)) {
            return false;
        }

        if (!matchesRole(rule, context)) {
            return false;
        }

        if (!matchesResources(rule, context)) {
            return false;
        }

        return true;
    }

    private boolean matchesTool(ToolPolicyRule rule, ToolInvocation invocation) {
        if (rule.tools().isEmpty()) {
            return true;
        }

        return rule.tools().stream()
                .anyMatch(pattern -> ToolNameMatcher.matches(pattern, invocation.name()));
    }

    private boolean matchesTenant(ToolPolicyRule rule, PolicyEvaluationContext context) {
        if (rule.tenants().isEmpty()) {
            return true;
        }

        String tenantId = context.tenantId();
        return tenantId != null && rule.tenants().contains(tenantId);
    }

    private boolean matchesUser(ToolPolicyRule rule, PolicyEvaluationContext context) {
        if (rule.users().isEmpty()) {
            return true;
        }

        String userId = context.userId();
        return userId != null && rule.users().contains(userId);
    }

    private boolean matchesRole(ToolPolicyRule rule, PolicyEvaluationContext context) {
        if (rule.roles().isEmpty()) {
            return true;
        }

        if (context.roles().isEmpty()) {
            return false;
        }

        return context.roles().stream().anyMatch(r -> rule.roles().contains(r));
    }

    private boolean matchesResources(ToolPolicyRule rule, PolicyEvaluationContext context) {
        if (rule.resources().isEmpty()) {
            return true;
        }

        // Check each resource constraint defined in the rule
        for (Map.Entry<String, Set<String>> entry : rule.resources().entrySet()) {
            String permissionId = entry.getKey();
            Set<String> allowedScopes = entry.getValue();

            var requestedResource = context.resource(permissionId);
            if (requestedResource.isPresent()) {
                String resource = requestedResource.get();
                boolean matched = allowedScopes.stream().anyMatch(scope -> matchesScope(scope, resource));
                if (!matched) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean matchesScope(String scope, String resource) {
        if (scope == null || resource == null) {
            return false;
        }
        if ("*".equals(scope) || "**".equals(scope)) {
            return true;
        }
        if (scope.endsWith("/**")) {
            String prefix = scope.substring(0, scope.length() - 3);
            return resource.startsWith(prefix);
        }
        if (scope.endsWith("/*")) {
            String prefix = scope.substring(0, scope.length() - 2);
            return resource.startsWith(prefix);
        }
        return scope.equals(resource);
    }

    private PolicyDecision decision(ToolPolicyRule rule) {
        return switch (rule.effect()) {
            case ALLOW -> PolicyDecision.allow(rule.id(), "Allowed by policy rule: " + rule.id());
            case DENY -> PolicyDecision.deny(
                    PolicyDecisionReason.POLICY_DENIED,
                    rule.id(),
                    "Denied by policy rule: " + rule.id()
            );
            case REQUIRE_APPROVAL -> PolicyDecision.requireApproval(
                    rule.id(),
                    null,
                    "Approval required by policy rule: " + rule.id()
            );
        };
    }
}
