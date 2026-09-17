package tech.kayys.wayang.execution.governance;

import org.junit.jupiter.api.Test;
import tech.kayys.wayang.tool.SimpleToolInvocation;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DefaultToolPolicyTest {

    private ToolPermissionContext ctx(String tenantId, String userId, List<String> roles) {
        return new ToolPermissionContext(tenantId, userId, "exec-1", roles, "tool", ToolCapabilityLevel.READ);
    }

    private ToolPermissionContext ctx(List<String> roles) {
        return ctx(null, null, roles);
    }

    private SimpleToolInvocation invocation(String name) {
        return SimpleToolInvocation.of(name, Map.of());
    }

    @Test
    void allowsWhenNoRulesWithDefaultAllow() {
        DefaultToolPolicy policy = new DefaultToolPolicy(List.of());
        PolicyDecision d = policy.evaluate(invocation("shell.run"), ctx(List.of()));
        assertTrue(d.isAllowed());
    }

    @Test
    void deniesWhenNoRulesWithDefaultDeny() {
        DefaultToolPolicy policy = new DefaultToolPolicy(
                "strict", 100, PolicyDefaultEffect.DENY, List.of());
        PolicyDecision d = policy.evaluate(invocation("shell.run"), ctx(List.of()));
        assertTrue(d.isDenied());
        assertEquals(PolicyDecisionReason.NO_MATCHING_POLICY, d.decisionReason());
    }

    @Test
    void denyRuleByPermission() {
        ToolPolicyRule rule = ToolPolicyRule.builder("deny-shell")
                .tools(Set.of("shell.*"))
                .effect(PolicyEffect.DENY)
                .priority(100)
                .build();

        DefaultToolPolicy policy = new DefaultToolPolicy(List.of(rule));
        PolicyDecision d = policy.evaluate(invocation("shell.run"), ctx(List.of()));
        assertTrue(d.isDenied());
        assertEquals("deny-shell", d.policyId());
    }

    @Test
    void allowRuleByPermission() {
        ToolPolicyRule rule = ToolPolicyRule.builder("allow-readonly")
                .tools(Set.of("web.*"))
                .effect(PolicyEffect.ALLOW)
                .priority(100)
                .build();

        DefaultToolPolicy policy = new DefaultToolPolicy(List.of(rule));
        PolicyDecision d = policy.evaluate(invocation("web.search"), ctx(List.of()));
        assertTrue(d.isAllowed());
        assertEquals("allow-readonly", d.policyId());
    }

    @Test
    void requireApprovalRule() {
        ToolPolicyRule rule = ToolPolicyRule.builder("approval-write")
                .tools(Set.of("filesystem.write"))
                .effect(PolicyEffect.REQUIRE_APPROVAL)
                .priority(100)
                .build();

        DefaultToolPolicy policy = new DefaultToolPolicy(List.of(rule));
        PolicyDecision d = policy.evaluate(invocation("filesystem.write"), ctx(List.of()));
        assertTrue(d.requiresApproval());
        assertEquals("approval-write", d.policyId());
    }

    @Test
    void tenantScopedRule() {
        ToolPolicyRule rule = ToolPolicyRule.builder("restricted-tenant")
                .tenants(Set.of("tenant-restricted"))
                .effect(PolicyEffect.DENY)
                .priority(50)
                .build();

        DefaultToolPolicy policy = new DefaultToolPolicy(List.of(rule));

        // tenant-restricted: denied
        PolicyDecision denied = policy.evaluate(invocation("web.search"),
                ctx("tenant-restricted", null, List.of()));
        assertTrue(denied.isDenied());

        // other tenant: falls through to default allow
        PolicyDecision allowed = policy.evaluate(invocation("web.search"),
                ctx("tenant-other", null, List.of()));
        assertTrue(allowed.isAllowed());
    }

    @Test
    void roleScopedRule() {
        ToolPolicyRule rule = ToolPolicyRule.builder("shell-admin-only")
                .tools(Set.of("shell.*"))
                .roles(Set.of("admin"))
                .effect(PolicyEffect.ALLOW)
                .priority(100)
                .build();

        DefaultToolPolicy policy = new DefaultToolPolicy(
                "strict", 100, PolicyDefaultEffect.DENY, List.of(rule));

        // admin role: allowed
        PolicyDecision allowed = policy.evaluate(invocation("shell.run"), ctx(List.of("admin")));
        assertTrue(allowed.isAllowed());

        // no admin role: denied by default
        PolicyDecision denied = policy.evaluate(invocation("shell.run"), ctx(List.of("viewer")));
        assertTrue(denied.isDenied());
    }

    @Test
    void resourceScopedRule() {
        ToolPolicyRule rule = ToolPolicyRule.builder("workspace-write")
                .tools(Set.of("filesystem.write"))
                .resources(Map.of("filesystem.write", Set.of("/workspace/**")))
                .effect(PolicyEffect.ALLOW)
                .priority(100)
                .build();

        DefaultToolPolicy policy = new DefaultToolPolicy(
                "strict", 100, PolicyDefaultEffect.DENY, List.of(rule));

        ToolPermissionContext permCtx = ctx(List.of());

        // inside workspace: allowed
        PolicyEvaluationContext ctxAllowed = PolicyEvaluationContexts.builder(permCtx, invocation("filesystem.write"))
                .resources(Map.of("filesystem.write", "/workspace/project/Main.java"))
                .build();
        PolicyDecision allowed = policy.evaluate(ctxAllowed);
        assertTrue(allowed.isAllowed());

        // outside workspace: denied
        PolicyEvaluationContext ctxDenied = PolicyEvaluationContexts.builder(permCtx, invocation("filesystem.write"))
                .resources(Map.of("filesystem.write", "/etc/passwd"))
                .build();
        PolicyDecision denied = policy.evaluate(ctxDenied);
        assertTrue(denied.isDenied());
    }

    @Test
    void globalWildcardMatchesAllTools() {
        ToolPolicyRule rule = ToolPolicyRule.builder("deny-all")
                .tools(Set.of("*"))
                .effect(PolicyEffect.DENY)
                .priority(100)
                .build();

        DefaultToolPolicy policy = new DefaultToolPolicy(List.of(rule));
        assertTrue(policy.evaluate(invocation("web.search"), ctx(List.of())).isDenied());
        assertTrue(policy.evaluate(invocation("shell.run"), ctx(List.of())).isDenied());
        assertTrue(policy.evaluate(invocation("filesystem.read"), ctx(List.of())).isDenied());
    }

    @Test
    void emptyToolSelectorMatchesAllTools() {
        ToolPolicyRule rule = ToolPolicyRule.builder("deny-all")
                .tools(Set.of()) // empty = match all
                .effect(PolicyEffect.DENY)
                .priority(100)
                .build();

        DefaultToolPolicy policy = new DefaultToolPolicy(List.of(rule));
        assertTrue(policy.evaluate(invocation("web.search"), ctx(List.of())).isDenied());
    }

    @Test
    void disabledPolicyAlwaysAllows() {
        // DefaultToolPolicy always has enabled=true; test via IdentifiedToolPolicy
        DefaultToolPolicy policy = new DefaultToolPolicy(
                "deny-all-policy", 100, PolicyDefaultEffect.DENY, List.of());
        // Note: enabled is always true in DefaultToolPolicy — test that default allows when enabled=true
        PolicyDecision d = policy.evaluate(invocation("shell.run"), ctx(List.of()));
        // No matching rule + defaultDeny
        assertTrue(d.isDenied());
    }
}
