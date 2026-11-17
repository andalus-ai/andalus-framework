package tech.kayys.andalus.harness.planning;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default {@link PlanningRuntime} implementation.
 *
 * <p>The runtime validates planner output and records accepted revisions through
 * an injected {@link PlanRevisionHistory}. It never executes plan steps.</p>
 */
public class DefaultPlanningRuntime implements PlanningRuntime {

    private static final Logger LOG = Logger.getLogger(DefaultPlanningRuntime.class.getName());

    private final PlannerRegistry registry;
    private final PlanValidator validator;
    private final PlanRevisionHistory revisionHistory;
    private final PlanRevisionPolicy revisionPolicy;

    public DefaultPlanningRuntime(
            PlannerRegistry registry,
            PlanValidator validator,
            PlanRevisionHistory revisionHistory
    ) {
        this(registry, validator, revisionHistory, PlanRevisionPolicy.PRESERVE_COMPLETED_STEPS);
    }

    /**
     * Creates a runtime with an explicit policy for replacing previously completed work.
     */
    public DefaultPlanningRuntime(
            PlannerRegistry registry,
            PlanValidator validator,
            PlanRevisionHistory revisionHistory,
            PlanRevisionPolicy revisionPolicy
    ) {
        if (registry == null) throw new IllegalArgumentException("registry must not be null");
        if (validator == null) throw new IllegalArgumentException("validator must not be null");
        if (revisionHistory == null) throw new IllegalArgumentException("revisionHistory must not be null");
        if (revisionPolicy == null) throw new IllegalArgumentException("revisionPolicy must not be null");
        this.registry = registry;
        this.validator = validator;
        this.revisionHistory = revisionHistory;
        this.revisionPolicy = revisionPolicy;
    }

    /** Convenience constructor using the default validator and process-local revision history. */
    public DefaultPlanningRuntime(PlannerRegistry registry, PlanValidator validator) {
        this(registry, validator, new InMemoryPlanRevisionHistory());
    }

    /** Convenience constructor using the default validator and process-local revision history. */
    public DefaultPlanningRuntime(PlannerRegistry registry) {
        this(registry, new DefaultPlanValidator(), new InMemoryPlanRevisionHistory());
    }

    /** Exposes the configured history store for inspection and operational integration. */
    public PlanRevisionHistory revisionHistory() {
        return revisionHistory;
    }

    /** Returns the configured completed-step continuity policy. */
    public PlanRevisionPolicy revisionPolicy() {
        return revisionPolicy;
    }

    @Override
    public CompletionStage<PlanningResult> createPlan(PlanningRequest request) {
        if (request == null) {
            return CompletableFuture.completedFuture(PlanningResult.failed("Planning request must not be null."));
        }
        Planner planner = resolveFirst();
        if (planner == null) {
            return CompletableFuture.completedFuture(PlanningResult.failed("No planner is registered."));
        }
        return invokePlanner(() -> planner.plan(request), this::validateIfSuccessful);
    }

    @Override
    public CompletionStage<PlanningResult> revisePlan(PlanRevisionRequest request) {
        if (request == null) {
            return CompletableFuture.completedFuture(PlanningResult.failed("Plan revision request must not be null."));
        }
        Planner planner = resolveFirst();
        if (planner == null) {
            return CompletableFuture.completedFuture(PlanningResult.failed("No planner is registered."));
        }
        return invokePlanner(
                () -> planner.revise(request),
                result -> validateRevisionIfSuccessful(result, request));
    }

    private Planner resolveFirst() {
        return registry.all().stream().findFirst().orElse(null);
    }

    private CompletionStage<PlanningResult> invokePlanner(
            java.util.function.Supplier<CompletionStage<PlanningResult>> supplier,
            java.util.function.Function<PlanningResult, PlanningResult> resultValidator) {
        try {
            return supplier.get().thenApply(resultValidator)
                    .exceptionally(ex -> {
                        LOG.log(Level.WARNING, "Planner invocation or revision recording failed", ex);
                        return PlanningResult.failed("Planner error: " + ex.getMessage());
                    });
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "Planner invocation failed synchronously", ex);
            return CompletableFuture.completedFuture(
                    PlanningResult.failed("Planner invocation error: " + ex.getMessage()));
        }
    }

    private PlanningResult validateIfSuccessful(PlanningResult result) {
        if (result == null) {
            return PlanningResult.failed("Planner returned a null result.");
        }
        if (!result.isSuccess()) {
            return result;
        }
        AgentPlan plan = result.plan().orElseThrow();
        return validatePlan(plan, result);
    }

    private PlanningResult validateRevisionIfSuccessful(PlanningResult result, PlanRevisionRequest request) {
        if (result == null) {
            return PlanningResult.failed("Planner returned a null result.");
        }
        if (!result.isSuccess()) {
            return result;
        }

        AgentPlan currentPlan = request.currentPlan();
        AgentPlan revisedPlan = result.plan().orElseThrow();
        if (!currentPlan.id().equals(revisedPlan.id())) {
            return PlanningResult.invalid("Plan revision must preserve the original plan ID.");
        }
        if (!currentPlan.executionId().equals(revisedPlan.executionId())) {
            return PlanningResult.invalid("Plan revision must preserve the original execution ID.");
        }
        if (currentPlan.revision() == Long.MAX_VALUE
                || revisedPlan.revision() != currentPlan.revision() + 1) {
            return PlanningResult.invalid("Plan revision must increment the current revision by exactly one.");
        }

        PlanningResult validated = validatePlan(revisedPlan, result);
        if (!validated.isSuccess()) {
            return validated;
        }

        PlanRevisionReconciliation reconciliation =
                new PlanRevisionReconciler().reconcile(request, revisedPlan);
        if (revisionPolicy == PlanRevisionPolicy.PRESERVE_COMPLETED_STEPS
                && !reconciliation.consistent()) {
            return PlanningResult.invalid(
                    "Plan revision continuity check failed: "
                            + String.join("; ", reconciliation.errors()));
        }

        revisionHistory.append(PlanRevisionRecord.of(
                currentPlan, revisedPlan, request.revisionReason(), request.feedback()));
        return validated;
    }

    private PlanningResult validatePlan(AgentPlan plan, PlanningResult originalResult) {
        PlanValidationResult validation = validator.validate(plan);
        if (!validation.valid()) {
            String reason = "Plan validation failed: " + String.join("; ", validation.errors());
            return PlanningResult.invalid(reason);
        }
        return originalResult;
    }
}
