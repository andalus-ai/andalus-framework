package tech.kayys.andalus.harness.governance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.harness.context.DefaultHarnessContext;
import tech.kayys.andalus.harness.context.DefaultHarnessIdentity;
import tech.kayys.andalus.harness.context.DefaultHarnessSession;
import tech.kayys.andalus.harness.environment.CapabilityId;
import tech.kayys.andalus.harness.environment.DefaultHarnessCapabilities;
import tech.kayys.andalus.harness.environment.DefaultHarnessEnvironment;
import tech.kayys.andalus.harness.environment.DefaultHarnessResources;
import tech.kayys.andalus.governance.action.ActionExecutionResult;
import tech.kayys.andalus.governance.action.DefaultHarnessAction;
import tech.kayys.andalus.governance.action.HarnessAction;
import tech.kayys.andalus.governance.approval.ApprovalContext;
import tech.kayys.andalus.governance.approval.ApprovalRequest;
import tech.kayys.andalus.governance.approval.ApprovalResolution;
import tech.kayys.andalus.governance.approval.InMemoryHarnessApproval;
import tech.kayys.andalus.harness.governance.execution.DefaultHarnessActionExecutor;
import tech.kayys.andalus.governance.policy.AllowDecision;
import tech.kayys.andalus.governance.policy.ApprovalDecision;
import tech.kayys.andalus.governance.policy.DenyDecision;
import tech.kayys.andalus.harness.lifecycle.DefaultHarnessLifecycle;
import tech.kayys.andalus.harness.lifecycle.HarnessExecutionStatus;
import tech.kayys.andalus.harness.runtime.HarnessRuntime;
import tech.kayys.andalus.harness.runtime.RuntimeBackedHarnessRuntime;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class HarnessActionExecutorTest {

    private DefaultHarnessCapabilities capabilities;
    private DefaultHarnessEnvironment environment;
    private DefaultHarnessContext context;
    private DefaultHarnessLifecycle lifecycle;
    private InMemoryHarnessApproval approval;

    @BeforeEach
    void setUp() {
        DefaultHarnessIdentity identity = DefaultHarnessIdentity.of("test-agent");
        DefaultHarnessSession session = DefaultHarnessSession.createNew();
        capabilities = new DefaultHarnessCapabilities(Set.of(CapabilityId.of("fs.read"), CapabilityId.of("fs.write")));
        DefaultHarnessResources resources = new DefaultHarnessResources();
        environment = new DefaultHarnessEnvironment(identity, session, capabilities, resources);
        context = new DefaultHarnessContext(identity, session, Map.of());
        lifecycle = new DefaultHarnessLifecycle(HarnessExecutionStatus.RUNNING);
        approval = new InMemoryHarnessApproval();
    }

    @Test
    void testUnauthorizedCapabilityBlocked() {
        DefaultHarnessActionExecutor executor = new DefaultHarnessActionExecutor(
                (ctx, action) -> AllowDecision.of("p", "ok"),
                approval
        );
        HarnessRuntime runtime = new RuntimeBackedHarnessRuntime(environment, context, lifecycle);

        // Request capability not in capabilities set
        HarnessAction action = DefaultHarnessAction.of("exec", "network.http");
        ActionExecutionResult result = executor.execute(action, runtime);

        assertFalse(result.success());
        assertTrue(result.errorMessage().orElse("").contains("unauthorized capability"));
    }

    @Test
    void testPolicyDenyBlocked() {
        DefaultHarnessActionExecutor executor = new DefaultHarnessActionExecutor(
                (ctx, action) -> DenyDecision.of("strict-policy", "Blocked by security rule"),
                approval
        );
        HarnessRuntime runtime = new RuntimeBackedHarnessRuntime(environment, context, lifecycle);

        HarnessAction action = DefaultHarnessAction.of("write", "fs.write");
        ActionExecutionResult result = executor.execute(action, runtime);

        assertFalse(result.success());
        assertTrue(result.errorMessage().orElse("").contains("Blocked by security rule"));
    }

    @Test
    void testPolicyApprovalFlow() {
        HarnessAction action = DefaultHarnessAction.of("write", "fs.write");
        ApprovalRequest req = approval.create(action, ApprovalContext.of("agent"));

        DefaultHarnessActionExecutor executor = new DefaultHarnessActionExecutor(
                (ctx, act) -> ApprovalDecision.of("sensitive-policy", "Approval required for write", req),
                approval
        );
        HarnessRuntime runtime = new RuntimeBackedHarnessRuntime(environment, context, lifecycle);

        // Before approval: should be suspended
        ActionExecutionResult pendingResult = executor.execute(action, runtime);
        assertFalse(pendingResult.success());
        assertTrue(pendingResult.errorMessage().orElse("").contains("requires approval"));

        // Grant approval
        approval.resolve(req.id(), ApprovalResolution.APPROVED);

        // After approval: should pass through gate
        ActionExecutionResult approvedResult = executor.execute(action, runtime);
        assertTrue(approvedResult.success());
    }
}
