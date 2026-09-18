package tech.kayys.wayang.harness.model;

import tech.kayys.wayang.harness.capability.CapabilityDecisionType;
import tech.kayys.wayang.harness.capability.CapabilityRequest;
import tech.kayys.wayang.harness.capability.CapabilityScope;
import tech.kayys.wayang.harness.context.DefaultHarnessIdentity;
import tech.kayys.wayang.harness.context.DefaultHarnessSession;
import tech.kayys.wayang.harness.context.HarnessIdentity;
import tech.kayys.wayang.harness.context.HarnessSession;
import tech.kayys.wayang.harness.environment.HarnessEnvironment;
import tech.kayys.wayang.harness.execution.action.ActionExecutionMode;
import tech.kayys.wayang.harness.execution.action.ActionId;
import tech.kayys.wayang.harness.execution.action.ActionJournal;
import tech.kayys.wayang.harness.execution.action.ActionRecord;
import tech.kayys.wayang.harness.execution.state.ExecutionId;
import tech.kayys.wayang.harness.governance.action.ActionMetadata;
import tech.kayys.wayang.harness.governance.action.DefaultHarnessAction;
import tech.kayys.wayang.harness.governance.action.HarnessAction;
import tech.kayys.wayang.harness.governance.budget.*;
import tech.kayys.wayang.harness.governance.policy.DenyDecision;
import tech.kayys.wayang.harness.governance.policy.PolicyChain;
import tech.kayys.wayang.harness.governance.policy.PolicyContext;
import tech.kayys.wayang.harness.governance.policy.PolicyDecision;
import tech.kayys.wayang.harness.workspace.WorkspaceId;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Provides governed model executor behavior for the Wayang framework.
 */


public class GovernedModelExecutor {

    private final ModelCatalog catalog;
    private final ModelRouter router;
    private final CapabilityScope capabilityScope;
    private final PolicyChain policyChain;
    private final BudgetManager budgetManager;
    private final ActionJournal journal;

    public GovernedModelExecutor(
            ModelCatalog catalog,
            ModelRouter router,
            CapabilityScope capabilityScope,
            PolicyChain policyChain,
            BudgetManager budgetManager,
            ActionJournal journal
    ) {
        this.catalog = Objects.requireNonNull(catalog, "catalog cannot be null");
        this.router = Objects.requireNonNull(router, "router cannot be null");
        this.capabilityScope = capabilityScope;
        this.policyChain = policyChain;
        this.budgetManager = budgetManager;
        this.journal = journal;
    }

    public ModelResult execute(ModelIntent intent, ModelExecutionContext context) {
        Objects.requireNonNull(intent, "intent cannot be null");
        ModelInvocationId invocationId = ModelInvocationId.generate();

        // 1. Capability check
        if (capabilityScope != null) {
            String cap = "model." + intent.task().name().toLowerCase();
            var capDecision = capabilityScope.evaluate(CapabilityRequest.of(cap, "infer", Map.of()));
            if (capDecision.type() == CapabilityDecisionType.DENY) {
                return ModelResult.denied(invocationId, "Model capability denied: " + capDecision.reason());
            }
        }

        // 2. Policy check
        if (policyChain != null) {
            HarnessAction action = DefaultHarnessAction.of("model", "model." + intent.task().name().toLowerCase(), null, Map.of(), ActionMetadata.readOnly());
            PolicyContext policyContext = new PolicyContext() {
                @Override
                public HarnessIdentity identity() {
                    return context != null && context.identity() != null ? context.identity() : DefaultHarnessIdentity.of("anonymous");
                }

                @Override
                public HarnessSession session() {
                    return DefaultHarnessSession.createNew();
                }

                @Override
                public HarnessEnvironment environment() {
                    return null;
                }

                @Override
                public Optional<WorkspaceId> workspace() {
                    return Optional.empty();
                }

                @Override
                public Map<String, Object> attributes() {
                    return Map.of("task", intent.task().name());
                }
            };
            PolicyDecision decision = policyChain.evaluate(policyContext, action);
            if (decision instanceof DenyDecision deny) {
                return ModelResult.denied(invocationId, "Policy denied model inference: " + deny.reason());
            }
        }

        // 3. Budget reservation (estimate tokens)
        BudgetReservation reservation = null;
        if (budgetManager != null) {
            try {
                reservation = budgetManager.reserve(new BudgetRequest(
                        BudgetDimension.MODEL_OUTPUT_TOKENS,
                        BudgetAmount.of(1000, BudgetDimension.MODEL_OUTPUT_TOKENS.unit()),
                        intent.task().name()
                ));
            } catch (Exception e) {
                return ModelResult.denied(invocationId, "Budget exceeded for model tokens: " + e.getMessage());
            }
        }

        // 4. Model resolution
        ModelRoutingContext routingContext = new ModelRoutingContext(
                context != null ? context.identity() : null,
                context != null ? context.resources() : null,
                null
        );
        Optional<ModelResolution> resolution = router.resolve(intent, routingContext);
        if (resolution.isEmpty()) {
            return ModelResult.failure(invocationId, "No eligible model found for task: " + intent.task(), ModelMetadata.cloud("generic"));
        }

        ModelDescriptor descriptor = resolution.get().model();
        ModelProvider provider = catalog.providerFor(descriptor.id()).orElse(null);
        if (provider == null) {
            return ModelResult.failure(invocationId, "Provider unavailable for model: " + descriptor.id().value(), descriptor.metadata());
        }

        // 5. Execution & Journal
        ActionId actionId = ActionId.generate();
        ExecutionId execId = context != null ? context.executionId() : ExecutionId.generate();
        ActionRecord actionRecord = ActionRecord.started(actionId, execId, "model:" + descriptor.name(), ActionExecutionMode.PURE);
        if (journal != null) {
            journal.started(actionRecord);
        }

        ModelInvocation invocation = new ModelInvocation(invocationId, descriptor.id(), intent.input(), GenerationParameters.defaults());
        ModelExecutor executor = provider.executor(descriptor.id());

        Instant start = Instant.now();
        ModelResult result;
        try {
            result = executor.execute(invocation, context);
        } catch (Throwable t) {
            result = ModelResult.failure(invocationId, t.getMessage() != null ? t.getMessage() : t.getClass().getSimpleName(), descriptor.metadata());
        }

        Duration duration = Duration.between(start, Instant.now());

        // 6. Settle budget
        if (budgetManager != null && reservation != null) {
            long tokensUsed = result.usage().outputTokens() > 0 ? result.usage().outputTokens() : 100;
            budgetManager.settle(reservation, BudgetAmount.of(tokensUsed, BudgetDimension.MODEL_OUTPUT_TOKENS.unit()));
        }

        // 7. Journal completion
        if (journal != null) {
            if (result.isSuccess()) {
                journal.completed(actionId, result.output().text());
            } else {
                journal.failed(actionId, result.output().text());
            }
        }

        return result;
    }
}
