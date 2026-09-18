package tech.kayys.wayang.harness.tool;

import tech.kayys.wayang.harness.capability.CapabilityDecisionType;
import tech.kayys.wayang.harness.capability.CapabilityRequest;
import tech.kayys.wayang.harness.capability.CapabilityScope;
import tech.kayys.wayang.harness.context.DefaultHarnessIdentity;
import tech.kayys.wayang.harness.context.DefaultHarnessSession;
import tech.kayys.wayang.harness.context.HarnessIdentity;
import tech.kayys.wayang.harness.context.HarnessSession;
import tech.kayys.wayang.harness.environment.HarnessEnvironment;
import tech.kayys.wayang.harness.execution.action.*;
import tech.kayys.wayang.harness.execution.state.ExecutionId;
import tech.kayys.wayang.governance.action.ActionMetadata;
import tech.kayys.wayang.governance.action.DefaultHarnessAction;
import tech.kayys.wayang.governance.action.HarnessAction;
import tech.kayys.wayang.governance.approval.ApprovalRequest;
import tech.kayys.wayang.governance.approval.ApprovalStatus;
import tech.kayys.wayang.governance.approval.HarnessApproval;
import tech.kayys.wayang.governance.budget.*;
import tech.kayys.wayang.governance.policy.*;
import tech.kayys.wayang.harness.memory.MemoryStore;
import tech.kayys.wayang.harness.resource.ResourceScope;
import tech.kayys.wayang.harness.workspace.WorkspaceId;
import tech.kayys.wayang.tool.*;
import tech.kayys.wayang.tool.catalog.ToolCatalog;
import tech.kayys.wayang.tool.event.ToolEvent;
import tech.kayys.wayang.tool.event.ToolEventType;
import tech.kayys.wayang.tool.resolution.ToolIntent;
import tech.kayys.wayang.tool.resolution.ToolResolution;
import tech.kayys.wayang.tool.resolution.ToolResolutionContext;
import tech.kayys.wayang.tool.resolution.ToolResolver;
import tech.kayys.wayang.tool.scheduling.ToolScheduler;
import tech.kayys.wayang.tool.validator.ToolInputValidator;
import tech.kayys.wayang.tool.validator.ToolOutputValidator;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

/**
 * Provides governed tool executor behavior for the Wayang framework.
 */
public class GovernedToolExecutor {

    private final ToolCatalog catalog;
    private final ToolResolver resolver;
    private final CapabilityScope capabilityScope;
    private final PolicyChain policyChain;
    private final BudgetManager budgetManager;
    private final HarnessApproval approval;
    private final ActionJournal journal;
    private final MemoryStore memoryStore;
    private final ToolInputValidator inputValidator;
    private final ToolOutputValidator outputValidator;
    private final ToolResultRouter router;
    private final ToolScheduler scheduler;

    public GovernedToolExecutor(
            ToolCatalog catalog,
            ToolResolver resolver,
            CapabilityScope capabilityScope,
            PolicyChain policyChain,
            BudgetManager budgetManager,
            HarnessApproval approval,
            ActionJournal journal,
            MemoryStore memoryStore,
            ToolInputValidator inputValidator,
            ToolOutputValidator outputValidator,
            ToolResultRouter router,
            ToolScheduler scheduler
    ) {
        this.catalog = Objects.requireNonNull(catalog, "catalog cannot be null");
        this.resolver = Objects.requireNonNull(resolver, "resolver cannot be null");
        this.capabilityScope = capabilityScope;
        this.policyChain = policyChain;
        this.budgetManager = budgetManager;
        this.approval = approval;
        this.journal = journal;
        this.memoryStore = memoryStore;
        this.inputValidator = inputValidator != null ? inputValidator : ToolInputValidator.standard();
        this.outputValidator = outputValidator != null ? outputValidator : ToolOutputValidator.standard();
        this.router = router != null ? router : ToolResultRouter.standard();
        this.scheduler = scheduler != null ? scheduler : ToolScheduler.standard();
    }

    public ToolResult execute(ToolIntent intent, ToolExecutionContext context, Consumer<ToolEvent> eventConsumer) {
        Objects.requireNonNull(intent, "intent cannot be null");
        ToolInvocationId invocationId = ToolInvocationId.generate();

        // 1. Event: Tool requested
        if (eventConsumer != null) {
            eventConsumer.accept(ToolEvent.of(invocationId, ToolEventType.TOOL_REQUESTED, Map.of("capability", intent.capability())));
        }

        // 2. Capability verification
        if (capabilityScope != null) {
            var capDecision = capabilityScope.evaluate(CapabilityRequest.of(
                    intent.capability(),
                    "execute",
                    intent.arguments().asMap()
            ));
            if (capDecision.type() == CapabilityDecisionType.DENY) {
                return ToolResult.denied(invocationId, "Capability denied: " + capDecision.reason());
            }
        }

        HarnessAction action = DefaultHarnessAction.of("tool", intent.capability(), null, intent.arguments().asMap(), ActionMetadata.readOnly());

        // 3. Policy evaluation
        if (policyChain != null) {
            PolicyContext policyContext = (context instanceof PolicyContext pc) ? pc : new PolicyContext() {
                @Override
                public HarnessIdentity identity() {
                    return (context != null && context.identity() instanceof HarnessIdentity hi)
                            ? hi
                            : DefaultHarnessIdentity.of("anonymous");
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
                    return Map.of("capability", intent.capability(), "arguments", intent.arguments().asMap());
                }
            };

            PolicyDecision policyDecision = policyChain.evaluate(policyContext, action);
            if (policyDecision instanceof DenyDecision deny) {
                return ToolResult.denied(invocationId, "Policy denied: " + deny.reason());
            } else if (policyDecision instanceof ApprovalDecision app) {
                if (approval != null) {
                    ApprovalRequest req = ApprovalRequest.of(action, "Approval required for " + intent.capability() + ": " + app.reason());
                    ApprovalStatus status = approval.status(req.id());
                    if (status != ApprovalStatus.APPROVED) {
                        return ToolResult.requiresApproval(invocationId, "Approval required for " + intent.capability() + ": " + app.reason());
                    }
                } else {
                    return ToolResult.requiresApproval(invocationId, "Approval required: " + app.reason());
                }
            }
        }

        if (eventConsumer != null) {
            eventConsumer.accept(ToolEvent.of(invocationId, ToolEventType.TOOL_AUTHORIZED));
        }

        // 4. Budget reservation
        BudgetReservation reservation = null;
        if (budgetManager != null) {
            try {
                reservation = budgetManager.reserve(new BudgetRequest(
                        BudgetDimension.TOOL_CALLS,
                        BudgetAmount.of(1, BudgetDimension.TOOL_CALLS.unit()),
                        intent.capability()
                ));
            } catch (Exception e) {
                return ToolResult.denied(invocationId, "Budget exceeded for tool calls: " + e.getMessage());
            }
        }

        if (eventConsumer != null) {
            eventConsumer.accept(ToolEvent.of(invocationId, ToolEventType.TOOL_RESERVED));
        }

        // 5. Tool Resolution
        ToolResolutionContext resolutionContext = new ToolResolutionContext(
                context != null ? context.identity() : null,
                context != null ? context.resources() : null,
                null
        );
        Optional<ToolResolution> resolution = resolver.resolve(intent, resolutionContext);
        if (resolution.isEmpty()) {
            return ToolResult.failure(invocationId, "No tool found resolving capability: " + intent.capability(), Duration.ZERO, "resolver");
        }

        ToolDescriptor descriptor = resolution.get().tool();
        ToolProvider provider = catalog.providerFor(descriptor.idAsToolId()).orElse(null);
        if (provider == null) {
            return ToolResult.failure(invocationId, "No provider registered for tool: " + descriptor.idAsToolId().value(), Duration.ZERO, "catalog");
        }

        // 6. Input validation
        if (!inputValidator.validate(intent.arguments(), descriptor.toolInputSchema())) {
            return ToolResult.failure(invocationId, "Input validation failed against schema for tool: " + descriptor.name(), Duration.ZERO, "validator");
        }

        // 7. Execution & Action Journal
        ToolInvocation invocation = ToolInvocation.of(invocationId, descriptor.idAsToolId(), intent.arguments());
        ActionId actionId = ActionId.generate();
        ExecutionId execId = (context != null && context.executionId() != null)
                ? ExecutionId.of(context.executionId())
                : ExecutionId.generate();

        ActionRecord actionRecord = ActionRecord.started(actionId, execId, "tool:" + descriptor.name(), ActionExecutionMode.PURE);
        if (journal != null) {
            journal.started(actionRecord);
        }

        if (eventConsumer != null) {
            eventConsumer.accept(ToolEvent.of(invocationId, ToolEventType.TOOL_STARTED));
        }

        ToolExecutor executor = provider.executor(descriptor.idAsToolId());
        Instant start = Instant.now();
        ToolResult rawResult;
        try {
            rawResult = executor.executeBlocking(invocation, context);
        } catch (Throwable t) {
            rawResult = ToolResult.failure(invocationId, t.getMessage() != null ? t.getMessage() : t.getClass().getSimpleName(), Duration.between(start, Instant.now()), descriptor.name());
        }

        Duration duration = Duration.between(start, Instant.now());
        if (rawResult.toolMetadata().duration() == Duration.ZERO) {
            rawResult = new DefaultToolResult(rawResult.invocationId(), rawResult.status(), rawResult.output(), new ToolMetadata(duration, descriptor.name(), rawResult.toolMetadata().attributes()));
        }

        // Settle budget
        if (budgetManager != null && reservation != null) {
            budgetManager.settle(reservation, BudgetAmount.of(1, BudgetDimension.TOOL_CALLS.unit()));
        }

        // 8. Output validation & truncation
        ToolResult validatedResult = outputValidator.validate(rawResult, context);

        // 9. Journal completion
        if (journal != null) {
            if (validatedResult.isSuccess()) {
                journal.completed(actionId, validatedResult.output().preview());
            } else {
                journal.failed(actionId, validatedResult.output().preview());
            }
        }

        // 10. Routing (context / artifact / memory / events)
        router.route(validatedResult, intent, context, memoryStore, eventConsumer);

        return validatedResult;
    }
}
