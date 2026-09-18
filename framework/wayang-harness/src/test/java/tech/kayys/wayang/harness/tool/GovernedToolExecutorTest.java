package tech.kayys.wayang.harness.tool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.wayang.harness.capability.CapabilityScope;
import tech.kayys.wayang.harness.capability.DefaultCapabilityScope;
import tech.kayys.wayang.harness.context.DefaultHarnessIdentity;
import tech.kayys.wayang.harness.execution.action.ActionJournal;
import tech.kayys.wayang.harness.execution.action.InMemoryActionJournal;
import tech.kayys.wayang.harness.execution.state.ExecutionId;
import tech.kayys.wayang.harness.governance.budget.BudgetManager;
import tech.kayys.wayang.harness.governance.budget.DefaultBudgetLedger;
import tech.kayys.wayang.harness.governance.budget.DefaultBudgetManager;
import tech.kayys.wayang.harness.governance.budget.DefaultBudgetPolicy;
import tech.kayys.wayang.harness.governance.policy.AllowDecision;
import tech.kayys.wayang.harness.governance.policy.DenyDecision;
import tech.kayys.wayang.harness.governance.policy.PolicyChain;
import tech.kayys.wayang.harness.memory.InMemoryMemoryStore;
import tech.kayys.wayang.harness.memory.MemoryStore;
import tech.kayys.wayang.tool.*;
import tech.kayys.wayang.tool.catalog.ToolCatalog;
import tech.kayys.wayang.tool.event.ToolEvent;
import tech.kayys.wayang.tool.event.ToolEventType;
import tech.kayys.wayang.tool.resolution.DefaultToolResolver;
import tech.kayys.wayang.tool.resolution.ToolIntent;
import tech.kayys.wayang.tool.resolution.ToolResolver;
import tech.kayys.wayang.tool.schema.DefaultToolInputSchema;
import tech.kayys.wayang.tool.schema.SchemaProperty;
import tech.kayys.wayang.tool.schema.SchemaType;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class GovernedToolExecutorTest {

    private ToolCatalog catalog;
    private ToolResolver resolver;
    private ActionJournal journal;
    private MemoryStore memoryStore;
    private BudgetManager budgetManager;
    private List<ToolEvent> emittedEvents;

    @BeforeEach
    void setUp() {
        catalog = ToolCatalog.create();
        resolver = new DefaultToolResolver(catalog);
        journal = new InMemoryActionJournal();
        memoryStore = new InMemoryMemoryStore();
        DefaultBudgetLedger ledger = new DefaultBudgetLedger();
        budgetManager = new DefaultBudgetManager(ledger, new DefaultBudgetPolicy(ledger));
        emittedEvents = new CopyOnWriteArrayList<>();

        // Register dummy tool
        ToolDescriptor desc = DefaultToolDescriptor.builder(ToolId.of("calc-tool"), "Calculator")
                .capabilities(Set.of("math.calculate"))
                .inputSchema(DefaultToolInputSchema.of(Map.of(
                        "expr", SchemaProperty.required("expr", SchemaType.STRING, "expression")
                )))
                .build();

        ToolProvider provider = new ToolProvider() {
            @Override
            public Optional<ToolDescriptor> describe(ToolId id) {
                return id.equals(desc.idAsToolId()) ? Optional.of(desc) : Optional.empty();
            }

            @Override
            public Collection<ToolDescriptor> tools() {
                return Set.of(desc);
            }

            @Override
            public ToolExecutor executor(ToolId id) {
                return ToolExecutor.synchronous((inv, ctx) -> ToolResult.success(inv.invocationIdentifier(), "result=42", Duration.ofMillis(5), "Calculator"));
            }
        };
        catalog.register(provider);
    }

    @Test
    void shouldExecuteToolSuccessfullyEndToEnd() {
        GovernedToolExecutor executor = new GovernedToolExecutor(
                catalog,
                resolver,
                DefaultCapabilityScope.allowAll(),
                (ctx, act) -> AllowDecision.of("test-policy", "Allowed"),
                budgetManager,
                null,
                journal,
                memoryStore,
                null,
                null,
                null,
                null
        );

        ToolIntent intent = ToolIntent.of("math.calculate", ToolArguments.of(Map.of("expr", "6*7")), null);
        ToolExecutionContext ctx = DefaultToolExecutionContext.of("exec-1", DefaultHarnessIdentity.of("agent-1"), null);

        ToolResult result = executor.execute(intent, ctx, emittedEvents::add);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.output().payload()).isEqualTo("result=42");

        // Check events emitted
        assertThat(emittedEvents).extracting(ToolEvent::type)
                .contains(ToolEventType.TOOL_REQUESTED, ToolEventType.TOOL_AUTHORIZED, ToolEventType.TOOL_RESERVED, ToolEventType.TOOL_STARTED, ToolEventType.TOOL_COMPLETED);

        // Check action journal recorded
        assertThat(journal.history(ExecutionId.of(ctx.executionId()))).isNotEmpty();
    }

    @Test
    void shouldDenyWhenCapabilityScopeDenies() {
        CapabilityScope denyingScope = new DefaultCapabilityScope(Set.of(), Set.of("math.calculate"), Set.of(), null);
        GovernedToolExecutor executor = new GovernedToolExecutor(
                catalog, resolver, denyingScope, null, null, null, null, null, null, null, null, null
        );

        ToolIntent intent = ToolIntent.of("math.calculate", ToolArguments.of(Map.of("expr", "1+1")), null);
        ToolResult result = executor.execute(intent, null, emittedEvents::add);

        assertThat(result.status()).isEqualTo(ToolResultStatus.DENIED);
        assertThat(result.output().preview()).contains("denied");
    }

    @Test
    void shouldDenyWhenPolicyDenies() {
        PolicyChain denyingPolicy = (ctx, act) -> new DenyDecision("p-deny", "Forbidden by security policy");
        GovernedToolExecutor executor = new GovernedToolExecutor(
                catalog, resolver, null, denyingPolicy, null, null, null, null, null, null, null, null
        );

        ToolIntent intent = ToolIntent.of("math.calculate", ToolArguments.of(Map.of("expr", "1+1")), null);
        ToolResult result = executor.execute(intent, null, emittedEvents::add);

        assertThat(result.status()).isEqualTo(ToolResultStatus.DENIED);
        assertThat(result.output().preview()).contains("Forbidden by security policy");
    }

    @Test
    void shouldFailWhenRequiredInputArgumentMissing() {
        GovernedToolExecutor executor = new GovernedToolExecutor(
                catalog, resolver, null, null, null, null, null, null, null, null, null, null
        );

        // Missing required "expr" argument
        ToolIntent intent = ToolIntent.of("math.calculate", ToolArguments.empty(), null);
        ToolResult result = executor.execute(intent, null, emittedEvents::add);

        assertThat(result.status()).isEqualTo(ToolResultStatus.FAILURE);
        assertThat(result.output().preview()).contains("Input validation failed");
    }
}
