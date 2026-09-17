package tech.kayys.wayang.execution.governance;

import org.junit.jupiter.api.Test;
import tech.kayys.wayang.tool.SimpleToolInvocation;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PolicyEvaluationContextTest {

    private SimpleToolInvocation invocation(String name) {
        return SimpleToolInvocation.of(name, Map.of());
    }

    private ToolPermissionContext permCtx() {
        return ToolPermissionContext.standalone("exec-1", "web.search", ToolCapabilityLevel.READ);
    }

    @Test
    void createFromPermissionContext() {
        PolicyEvaluationContext ctx = PolicyEvaluationContexts.create(permCtx(), invocation("web.search"));
        assertNotNull(ctx);
        assertEquals("exec-1", ctx.executionId());
        assertNull(ctx.agentId());
        assertTrue(ctx.toolMatchesPermissionContext());
    }

    @Test
    void builderPreservesFields() {
        PolicyEvaluationContext ctx = PolicyEvaluationContexts
                .builder(permCtx(), invocation("web.search"))
                .agentId("my-agent")
                .correlationId("corr-123")
                .resources(Map.of("filesystem.write", "/workspace/file.java"))
                .build();

        assertEquals("my-agent", ctx.agentId());
        assertEquals("corr-123", ctx.correlationId());
        assertEquals("/workspace/file.java", ctx.resource("filesystem.write").orElse(null));
    }

    @Test
    void hasRoleWorks() {
        PolicyEvaluationContext ctx = PolicyEvaluationContexts
                .builder(permCtx(), invocation("shell.run"))
                .roles(Set.of("admin", "developer"))
                .build();

        assertTrue(ctx.hasRole("admin"));
        assertTrue(ctx.hasRole("developer"));
        assertFalse(ctx.hasRole("viewer"));
    }

    @Test
    void deadlineExceededWhenPast() {
        Instant past = Instant.now().minusSeconds(60);
        PolicyEvaluationContext ctx = PolicyEvaluationContexts
                .builder(permCtx(), invocation("web.search"))
                .deadline(past)
                .build();

        assertTrue(ctx.deadlineExceeded(Instant.now()));
    }

    @Test
    void deadlineNotExceededWhenFuture() {
        Instant future = Instant.now().plusSeconds(60);
        PolicyEvaluationContext ctx = PolicyEvaluationContexts
                .builder(permCtx(), invocation("web.search"))
                .deadline(future)
                .build();

        assertFalse(ctx.deadlineExceeded(Instant.now()));
    }

    @Test
    void deadlineNotExceededWhenNull() {
        PolicyEvaluationContext ctx = PolicyEvaluationContexts.create(permCtx(), invocation("web.search"));
        assertFalse(ctx.deadlineExceeded(Instant.now()));
    }

    @Test
    void resourceMapIsImmutable() {
        PolicyEvaluationContext ctx = PolicyEvaluationContexts
                .builder(permCtx(), invocation("web.search"))
                .resources(Map.of("filesystem.read", "/workspace"))
                .build();

        assertThrows(UnsupportedOperationException.class,
                () -> ctx.resources().put("new", "value"));
    }

    @Test
    void toolMatchesPermissionContextMismatch() {
        ToolPermissionContext pc = new ToolPermissionContext(null, null, "exec-1",
                java.util.List.of(), "other-tool", ToolCapabilityLevel.READ);
        PolicyEvaluationContext ctx = PolicyEvaluationContexts.create(pc, invocation("web.search"));
        assertFalse(ctx.toolMatchesPermissionContext());
    }

    @Test
    void optionalMethodsReturnEmpty() {
        PolicyEvaluationContext ctx = PolicyEvaluationContexts.create(permCtx(), invocation("web.search"));
        assertFalse(ctx.agentIdOptional().isPresent());
        assertFalse(ctx.correlationIdOptional().isPresent());
        assertFalse(ctx.parentExecutionIdOptional().isPresent());
        assertFalse(ctx.deadlineOptional().isPresent());
    }
}
