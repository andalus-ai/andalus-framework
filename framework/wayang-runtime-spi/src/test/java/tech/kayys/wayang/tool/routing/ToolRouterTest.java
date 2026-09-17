package tech.kayys.wayang.tool.routing;

import org.junit.jupiter.api.Test;
import tech.kayys.wayang.execution.governance.*;
import tech.kayys.wayang.execution.governance.approval.*;
import tech.kayys.wayang.tool.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class ToolRouterTest {

    private SimpleToolInvocation invocation(String name) {
        return SimpleToolInvocation.of(name, Map.of());
    }

    private ToolContext toolContext() {
        return new SimpleToolContext(Map.of("tenantId", "tenant-1"));
    }

    private ToolExecutor echoExecutor() {
        return (inv, ctx) -> CompletableFuture.completedFuture(
                SimpleToolResult.success(Map.of("output", "routed-" + inv.name()))
        );
    }

    private Tool mockTool(String name) {
        return new Tool() {
            @Override
            public ToolDescriptor descriptor() {
                return SimpleToolDescriptor.of(name, "Mock " + name, Map.of());
            }

            @Override
            public CompletableFuture<ToolResult> execute(ToolInvocation invocation, ToolContext context) {
                return echoExecutor().execute(invocation, context);
            }

            @Override
            public tech.kayys.wayang.identity.ResourceId id() {
                return null;
            }

            @Override
            public tech.kayys.wayang.extension.Metadata metadata() {
                return tech.kayys.wayang.extension.Metadata.builder().name(name).build();
            }

            @Override
            public tech.kayys.wayang.resource.ResourceType type() {
                return new tech.kayys.wayang.resource.ResourceType.Tool();
            }
        };
    }

    @Test
    void defaultToolRouterRoutesAndExecutes() throws Exception {
        ToolExecutionGuard guard = new DefaultToolExecutionGuard(
                new DefaultPolicyEvaluationContextFactory(),
                new DefaultToolPolicyEvaluator(List.of(new DefaultToolPolicy(List.of()))),
                new DefaultApprovalService(new InMemoryApprovalStore()),
                new DefaultApprovalBindingValidator()
        );

        ToolResolver resolver = inv -> new ResolvedTool(mockTool(inv.name()), echoExecutor(), "local-provider");
        DefaultToolRouter router = new DefaultToolRouter(resolver, guard);

        ToolResult result = router.execute(invocation("fs.read"), toolContext()).get();
        assertEquals("routed-fs.read", result.getOutputs().get("output"));
    }

    @Test
    void firstAvailableSelectorPicksFirst() {
        FirstAvailableToolProviderSelector selector = new FirstAvailableToolProviderSelector();
        ResolvedTool r1 = new ResolvedTool(mockTool("t1"), echoExecutor(), "p1");
        ResolvedTool r2 = new ResolvedTool(mockTool("t2"), echoExecutor(), "p2");

        ToolPermissionContext pc = ToolPermissionContext.standalone("exec-1", "t1", ToolCapabilityLevel.READ);
        PolicyEvaluationContext policyCtx = PolicyEvaluationContexts.create(pc, invocation("t1"));
        ToolRoutingContext ctx = new ToolRoutingContext(policyCtx);

        ResolvedTool selected = selector.select(List.of(r1, r2), ctx);
        assertEquals("p1", selected.providerId());
    }

    @Test
    void healthAwareSelectorFiltersUnhealthy() {
        ToolProviderAvailability availability = providerId -> "p2".equals(providerId);
        HealthAwareToolProviderSelector selector = new HealthAwareToolProviderSelector(availability);

        ResolvedTool r1 = new ResolvedTool(mockTool("t1"), echoExecutor(), "p1");
        ResolvedTool r2 = new ResolvedTool(mockTool("t2"), echoExecutor(), "p2");

        ToolPermissionContext pc = ToolPermissionContext.standalone("exec-1", "t1", ToolCapabilityLevel.READ);
        PolicyEvaluationContext policyCtx = PolicyEvaluationContexts.create(pc, invocation("t1"));
        ToolRoutingContext ctx = new ToolRoutingContext(policyCtx);

        ResolvedTool selected = selector.select(List.of(r1, r2), ctx);
        assertEquals("p2", selected.providerId());
    }

    @Test
    void healthAwareSelectorThrowsWhenNoneHealthy() {
        ToolProviderAvailability availability = providerId -> false;
        HealthAwareToolProviderSelector selector = new HealthAwareToolProviderSelector(availability);

        ResolvedTool r1 = new ResolvedTool(mockTool("t1"), echoExecutor(), "p1");

        ToolPermissionContext pc = ToolPermissionContext.standalone("exec-1", "t1", ToolCapabilityLevel.READ);
        PolicyEvaluationContext policyCtx = PolicyEvaluationContexts.create(pc, invocation("t1"));
        ToolRoutingContext ctx = new ToolRoutingContext(policyCtx);

        assertThrows(ToolRoutingException.class, () -> selector.select(List.of(r1), ctx));
    }
}
