package tech.kayys.wayang.execution.governance.approval;

import org.junit.jupiter.api.Test;
import tech.kayys.wayang.execution.governance.PolicyEvaluationContext;
import tech.kayys.wayang.execution.governance.PolicyEvaluationContexts;
import tech.kayys.wayang.execution.governance.ToolCapabilityLevel;
import tech.kayys.wayang.execution.governance.ToolPermissionContext;
import tech.kayys.wayang.tool.SimpleToolInvocation;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DefaultApprovalBindingValidatorTest {

    private final DefaultApprovalBindingValidator validator = new DefaultApprovalBindingValidator();

    private SimpleToolInvocation invocation(String name) {
        return SimpleToolInvocation.of(name, Map.of());
    }

    private PolicyEvaluationContext context(String tenantId, String agentId, String executionId) {
        ToolPermissionContext pc = new ToolPermissionContext(tenantId, "user-1", executionId, List.of(), "tool", ToolCapabilityLevel.READ);
        return PolicyEvaluationContexts.builder(pc, invocation("filesystem.write"))
                .agentId(agentId)
                .build();
    }

    private ApprovalRequest approvedRequest(String tool, String tenantId, String agentId, String executionId) {
        return new ApprovalRequest(
                "appr-1",
                invocation(tool),
                tenantId,
                "user-1",
                agentId,
                executionId,
                "corr-1",
                "reason",
                Instant.now().minusSeconds(60),
                Instant.now().plusSeconds(300),
                ApprovalStatus.APPROVED,
                "admin",
                Instant.now().minusSeconds(30),
                "ok",
                Map.of()
        );
    }

    @Test
    void validApprovalPasses() {
        ApprovalRequest req = approvedRequest("filesystem.write", "tenant-1", "agent-1", "exec-1");
        PolicyEvaluationContext ctx = context("tenant-1", "agent-1", "exec-1");

        assertDoesNotThrow(() -> validator.validate(req, invocation("filesystem.write"), ctx));
    }

    @Test
    void pendingStatusThrows() {
        ApprovalRequest req = new ApprovalRequest(
                "appr-1",
                invocation("filesystem.write"),
                "tenant-1",
                "user-1",
                "agent-1",
                "exec-1",
                "corr-1",
                "reason",
                Instant.now().minusSeconds(60),
                Instant.now().plusSeconds(300),
                ApprovalStatus.PENDING,
                null,
                null,
                null,
                Map.of()
        );
        PolicyEvaluationContext ctx = context("tenant-1", "agent-1", "exec-1");

        assertThrows(ApprovalBindingException.class, () -> validator.validate(req, invocation("filesystem.write"), ctx));
    }

    @Test
    void mismatchedToolThrows() {
        ApprovalRequest req = approvedRequest("filesystem.write", "tenant-1", "agent-1", "exec-1");
        PolicyEvaluationContext ctx = context("tenant-1", "agent-1", "exec-1");

        assertThrows(ApprovalBindingException.class, () -> validator.validate(req, invocation("shell.run"), ctx));
    }

    @Test
    void mismatchedTenantThrows() {
        ApprovalRequest req = approvedRequest("filesystem.write", "tenant-1", "agent-1", "exec-1");
        PolicyEvaluationContext ctx = context("tenant-2", "agent-1", "exec-1");

        assertThrows(ApprovalBindingException.class, () -> validator.validate(req, invocation("filesystem.write"), ctx));
    }
}
