package tech.kayys.wayang.harness.governance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.wayang.harness.context.DefaultHarnessIdentity;
import tech.kayys.wayang.harness.context.DefaultHarnessSession;
import tech.kayys.wayang.harness.environment.DefaultHarnessCapabilities;
import tech.kayys.wayang.harness.environment.DefaultHarnessEnvironment;
import tech.kayys.wayang.harness.environment.DefaultHarnessResources;
import tech.kayys.wayang.harness.governance.action.DefaultHarnessAction;
import tech.kayys.wayang.harness.governance.action.HarnessAction;
import tech.kayys.wayang.harness.governance.approval.ApprovalRequest;
import tech.kayys.wayang.harness.governance.policy.*;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PolicyChainTest {

    private PolicyContext context;

    @BeforeEach
    void setUp() {
        DefaultHarnessIdentity identity = DefaultHarnessIdentity.of("agent-test");
        DefaultHarnessSession session = DefaultHarnessSession.createNew();
        DefaultHarnessCapabilities capabilities = new DefaultHarnessCapabilities();
        DefaultHarnessResources resources = new DefaultHarnessResources();
        DefaultHarnessEnvironment env = new DefaultHarnessEnvironment(identity, session, capabilities, resources);

        context = new DefaultPolicyContext(identity, session, env, Optional.empty(), Map.of());
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
