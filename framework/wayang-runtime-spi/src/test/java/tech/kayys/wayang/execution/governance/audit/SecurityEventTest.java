package tech.kayys.wayang.execution.governance.audit;

import org.junit.jupiter.api.Test;
import tech.kayys.wayang.execution.governance.PolicyEvaluationContext;
import tech.kayys.wayang.execution.governance.PolicyEvaluationContexts;
import tech.kayys.wayang.execution.governance.ToolCapabilityLevel;
import tech.kayys.wayang.execution.governance.ToolPermissionContext;
import tech.kayys.wayang.tool.SimpleToolInvocation;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SecurityEventTest {

    @Test
    void testSecurityEventsFactoryAndAuditSink() {
        InMemoryAuditSink sink = new InMemoryAuditSink();

        SimpleToolInvocation invocation = SimpleToolInvocation.of("file.write", Map.of("path", "/tmp/x"));
        ToolPermissionContext permCtx = new ToolPermissionContext("tenant-1", "user-1", "exec-1", List.of("admin"), "file.write", ToolCapabilityLevel.WRITE);

        PolicyEvaluationContext context = PolicyEvaluationContexts.builder(permCtx, invocation)
                .tenantId("tenant-1")
                .userId("user-1")
                .agentId("agent-1")
                .executionId("exec-1")
                .correlationId("corr-1")
                .resources(Map.of("file.write", "/tmp/x"))
                .build();

        SecurityEvent started = SecurityEvents.invocationStarted(context, "local");
        sink.append(started);

        SecurityEvent allowed = SecurityEvents.invocationAllowed(context, "local");
        sink.append(allowed);

        SecurityEvent denied = SecurityEvents.invocationDenied(context, "policy-1", "Restricted path");
        sink.append(denied);

        SecurityEvent approvalReq = SecurityEvents.approvalRequired(context, "appr-1", "Needs manager signoff");
        sink.append(approvalReq);

        assertEquals(4, sink.events().size());

        SecurityEvent event0 = sink.events().get(0);
        assertEquals(SecurityEventType.TOOL_INVOCATION_STARTED, event0.type());
        assertEquals("tenant-1", event0.tenantId());
        assertEquals("file.write", event0.toolName());
        assertEquals("/tmp/x", event0.resources().get("file.write"));

        SecurityEvent event2 = sink.events().get(2);
        assertEquals(SecurityEventType.TOOL_INVOCATION_DENIED, event2.type());
        assertEquals("DENIED", event2.outcome());
        assertEquals("Restricted path", event2.reason());
        assertEquals("policy-1", event2.policyId());
    }
}
