package tech.kayys.wayang.harness.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.wayang.harness.capability.CapabilityScope;
import tech.kayys.wayang.harness.capability.DefaultCapabilityScope;
import tech.kayys.wayang.harness.context.DefaultHarnessIdentity;
import tech.kayys.wayang.harness.execution.action.ActionJournal;
import tech.kayys.wayang.harness.execution.action.InMemoryActionJournal;
import tech.kayys.wayang.harness.governance.budget.BudgetManager;
import tech.kayys.wayang.harness.governance.budget.DefaultBudgetLedger;
import tech.kayys.wayang.harness.governance.budget.DefaultBudgetManager;
import tech.kayys.wayang.harness.governance.budget.DefaultBudgetPolicy;
import tech.kayys.wayang.harness.governance.policy.AllowDecision;
import tech.kayys.wayang.harness.governance.policy.DenyDecision;
import tech.kayys.wayang.harness.governance.policy.PolicyChain;

import java.time.Duration;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class GovernedModelExecutorTest {

    private ModelCatalog catalog;
    private ModelRouter router;
    private ActionJournal journal;
    private BudgetManager budgetManager;

    @BeforeEach
    void setUp() {
        catalog = ModelCatalog.create();
        router = new DefaultModelRouter(catalog);
        journal = new InMemoryActionJournal();
        DefaultBudgetLedger ledger = new DefaultBudgetLedger();
        budgetManager = new DefaultBudgetManager(ledger, new DefaultBudgetPolicy(ledger));

        ModelDescriptor desc = DefaultModelDescriptor.builder(ModelId.of("mock-gpt"), "MockGPT")
                .tasks(Set.of(ModelTask.CHAT))
                .build();

        ModelProvider provider = new ModelProvider() {
            @Override
            public Collection<ModelDescriptor> models() {
                return Set.of(desc);
            }

            @Override
            public Optional<ModelDescriptor> describe(ModelId modelId) {
                return modelId.equals(desc.id()) ? Optional.of(desc) : Optional.empty();
            }

            @Override
            public ModelExecutor executor(ModelId modelId) {
                return (inv, ctx) -> ModelResult.success(
                        inv.id(),
                        "Thinking completed: response 42",
                        ModelUsage.of(10, 5, Duration.ofMillis(20)),
                        desc.metadata()
                );
            }
        };
        catalog.register(provider);
    }

    @Test
    void shouldExecuteModelInferenceSuccessfully() {
        GovernedModelExecutor executor = new GovernedModelExecutor(
                catalog,
                router,
                DefaultCapabilityScope.allowAll(),
                (ctx, act) -> AllowDecision.of("p1", "allowed"),
                budgetManager,
                journal
        );

        ModelIntent intent = ModelIntent.prompt("Hello world");
        ModelExecutionContext ctx = DefaultModelExecutionContext.of(null, DefaultHarnessIdentity.of("test-agent"), null);

        ModelResult result = executor.execute(intent, ctx);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.output().text()).contains("response 42");

        // Check action journal
        assertThat(journal.history(ctx.executionId())).isNotEmpty();
    }

    @Test
    void shouldDenyWhenCapabilityScopeDenies() {
        CapabilityScope denyingScope = new DefaultCapabilityScope(Set.of(), Set.of("model.chat"), Set.of(), null);
        GovernedModelExecutor executor = new GovernedModelExecutor(
                catalog, router, denyingScope, null, null, null
        );

        ModelIntent intent = ModelIntent.prompt("Hello");
        ModelResult result = executor.execute(intent, null);

        assertThat(result.status()).isEqualTo(ModelResultStatus.DENIED);
        assertThat(result.output().text()).contains("denied");
    }

    @Test
    void shouldDenyWhenPolicyDenies() {
        PolicyChain denyingPolicy = (ctx, act) -> new DenyDecision("p-deny", "Model call prohibited");
        GovernedModelExecutor executor = new GovernedModelExecutor(
                catalog, router, null, denyingPolicy, null, null
        );

        ModelIntent intent = ModelIntent.prompt("Hello");
        ModelResult result = executor.execute(intent, null);

        assertThat(result.status()).isEqualTo(ModelResultStatus.DENIED);
        assertThat(result.output().text()).contains("prohibited");
    }
}
