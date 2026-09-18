package tech.kayys.wayang.governance.policy;

import tech.kayys.wayang.governance.action.*;
import tech.kayys.wayang.governance.approval.*;
import tech.kayys.wayang.governance.policy.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PolicyChainTest {

    private PolicyContext context;

    @BeforeEach
    void setUp() {
        context = new DefaultPolicyContext("agent-test", "session-test", "env-test", Optional.empty(), Map.of());
    }

    @Test
    void testAllAllow() {
        HarnessPolicy p1 = (ctx, action) -> AllowDecision.of("p1", "ok");
        HarnessPolicy p2 = (ctx, action) -> AllowDecision.of("p2", "ok");

        DefaultPolicyChain chain = DefaultPolicyChain.of(p1, p2);
        HarnessAction action = DefaultHarnessAction.of("test", "test.cap");

        PolicyDecision decision = chain.evaluate(context, action);
        assertTrue(decision.isAllowed());
        assertFalse(decision.isDenied());
        assertFalse(decision.requiresApproval());
    }

    @Test
    void testDenyDominates() {
        HarnessPolicy p1 = (ctx, action) -> AllowDecision.of("p1", "ok");
        HarnessPolicy p2 = (ctx, action) -> ApprovalDecision.of("p2", "need approval", ApprovalRequest.of(action, "reason"));
        HarnessPolicy p3 = (ctx, action) -> DenyDecision.of("p3", "strictly forbidden");

        // Chain with ALLOW, APPROVAL, DENY
        DefaultPolicyChain chain = DefaultPolicyChain.of(p1, p2, p3);
        HarnessAction action = DefaultHarnessAction.of("test", "test.cap");

        PolicyDecision decision = chain.evaluate(context, action);
        assertTrue(decision.isDenied(), "DENY must dominate over ALLOW and APPROVAL");
        assertEquals("p3", decision.policyId());
    }

    @Test
    void testApprovalTakesPrecedenceOverAllow() {
        HarnessPolicy p1 = (ctx, action) -> AllowDecision.of("p1", "ok");
        HarnessPolicy p2 = (ctx, action) -> ApprovalDecision.of("p2", "human check required", ApprovalRequest.of(action, "reason"));

        DefaultPolicyChain chain = DefaultPolicyChain.of(p1, p2);
        HarnessAction action = DefaultHarnessAction.of("test", "test.cap");

        PolicyDecision decision = chain.evaluate(context, action);
        assertTrue(decision.requiresApproval());
        assertFalse(decision.isAllowed());
        assertFalse(decision.isDenied());
    }
}
