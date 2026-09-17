package tech.kayys.wayang.execution.governance;

import org.junit.jupiter.api.Test;
import tech.kayys.wayang.tool.SimpleToolInvocation;
import tech.kayys.wayang.tool.ToolInvocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DefaultToolPolicyEvaluatorTest {

    private static ToolInvocation invocation(String name) {
        return SimpleToolInvocation.of(name, Map.of());
    }

    private static ToolPolicy policy(String id, int priority, List<String> order, PolicyDecision decision) {
        return new ToolPolicy() {
            @Override
            public String id() {
                return id;
            }

            @Override
            public int priority() {
                return priority;
            }

            @Override
            public PolicyDecision evaluate(ToolInvocation invocation, ToolPermissionContext context) {
                order.add(id);
                return decision;
            }
        };
    }

    @Test
    void policiesAreEvaluatedByPriority() {
        var invocation = invocation("filesystem.read");
        var context = ToolPermissionContext.standalone("execution-1", "filesystem.read", ToolCapabilityLevel.READ);
        var order = new ArrayList<String>();

        ToolPolicy first = policy("first", 100, order, PolicyDecision.allow());
        ToolPolicy second = policy("second", 10, order, PolicyDecision.allow());
        ToolPolicy third = policy("third", 200, order, PolicyDecision.allow());

        var evaluator = new DefaultToolPolicyEvaluator(List.of(first, second, third));
        evaluator.evaluate(invocation, context);

        assertEquals(List.of("second", "first", "third"), order);
    }

    @Test
    void denyShortCircuitsLaterPolicies() {
        var invocation = invocation("process.execute");
        var context = ToolPermissionContext.standalone("execution-1", "process.execute", ToolCapabilityLevel.EXECUTE);
        var order = new ArrayList<String>();

        ToolPolicy deny = policy("deny-shell", 10, order, PolicyDecision.deny("shell execution forbidden", "deny-shell"));
        ToolPolicy later = policy("later-policy", 20, order, PolicyDecision.allow());

        var evaluator = new DefaultToolPolicyEvaluator(List.of(deny, later));
        PolicyDecision result = evaluator.evaluate(invocation, context);

        assertTrue(result.isDenied());
        assertEquals(List.of("deny-shell"), order);
    }

    @Test
    void approvalDoesNotHideLaterDeny() {
        var invocation = invocation("filesystem.write");
        var context = ToolPermissionContext.standalone("execution-1", "filesystem.write", ToolCapabilityLevel.WRITE);

        ToolPolicy approval = policy("approval", 10, new ArrayList<>(), PolicyDecision.requireApproval("human approval required", "approval-policy"));
        ToolPolicy deny = policy("deny", 20, new ArrayList<>(), PolicyDecision.deny("production path forbidden", "production-policy"));

        var evaluator = new DefaultToolPolicyEvaluator(List.of(approval, deny));
        PolicyDecision result = evaluator.evaluate(invocation, context);

        assertTrue(result.isDenied());
    }

    @Test
    void evaluateDetailedReturnsAllEvaluations() {
        var invocation = invocation("filesystem.read");
        var context = ToolPermissionContext.standalone("execution-1", "filesystem.read", ToolCapabilityLevel.READ);
        var order = new ArrayList<String>();

        ToolPolicy p1 = policy("p1", 10, order, PolicyDecision.allow("p1", "ok"));
        ToolPolicy p2 = policy("p2", 20, order, PolicyDecision.requireApproval("p2", "appr-1", "need check"));

        var evaluator = new DefaultToolPolicyEvaluator(List.of(p1, p2));
        PolicyEvaluationResult detailed = evaluator.evaluateDetailed(invocation, context);

        assertTrue(detailed.requiresApproval());
        assertEquals(2, detailed.evaluations().size());
        assertEquals("p1", detailed.evaluations().get(0).policyId());
        assertEquals("p2", detailed.evaluations().get(1).policyId());
    }
}
