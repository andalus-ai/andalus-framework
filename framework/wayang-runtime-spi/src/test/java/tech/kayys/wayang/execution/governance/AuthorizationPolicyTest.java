package tech.kayys.wayang.execution.governance;

import org.junit.jupiter.api.Test;
import tech.kayys.wayang.tool.SimpleToolInvocation;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AuthorizationPolicyTest {

    private SimpleToolInvocation invocation(String name) {
        return SimpleToolInvocation.of(name, Map.of());
    }

    private ToolPermissionContext ctx(String tenantId, List<String> roles) {
        return new ToolPermissionContext(tenantId, "user-1", "exec-1", roles, "tool", ToolCapabilityLevel.READ);
    }

    @Test
    void tenantPolicyEnforcement() {
        TenantToolPolicy policy = new TenantToolPolicy("tenant-p", Set.of("tenant-a", "tenant-b"), 50);

        PolicyDecision allowed = policy.evaluate(invocation("fs.read"), ctx("tenant-a", List.of()));
        assertTrue(allowed.isAllowed());

        PolicyDecision denied = policy.evaluate(invocation("fs.read"), ctx("tenant-c", List.of()));
        assertTrue(denied.isDenied());
    }

    @Test
    void agentPolicyEnforcement() {
        AgentToolPolicy policy = new AgentToolPolicy("agent-p", "coding-agent", Set.of("filesystem.*", "git.*"));

        ToolPermissionContext permCtx = ctx("tenant-a", List.of());

        // Applicable agent with allowed tool
        PolicyEvaluationContext ctxAllowed = PolicyEvaluationContexts.builder(permCtx, invocation("filesystem.read"))
                .agentId("coding-agent")
                .build();
        assertTrue(policy.evaluate(ctxAllowed).isAllowed());

        // Applicable agent with disallowed tool
        PolicyEvaluationContext ctxDenied = PolicyEvaluationContexts.builder(permCtx, invocation("process.execute"))
                .agentId("coding-agent")
                .build();
        assertTrue(policy.evaluate(ctxDenied).isDenied());

        // Different agent is not restricted by this policy
        PolicyEvaluationContext ctxOther = PolicyEvaluationContexts.builder(permCtx, invocation("process.execute"))
                .agentId("admin-agent")
                .build();
        assertTrue(policy.evaluate(ctxOther).isAllowed());
    }

    @Test
    void rolePolicyEnforcement() {
        RoleToolPolicy policy = new RoleToolPolicy("role-p", Set.of("admin", "developer"));

        PolicyDecision allowed = policy.evaluate(invocation("fs.write"), ctx("tenant-a", List.of("developer")));
        assertTrue(allowed.isAllowed());

        PolicyDecision denied = policy.evaluate(invocation("fs.write"), ctx("tenant-a", List.of("viewer")));
        assertTrue(denied.isDenied());
    }
}
