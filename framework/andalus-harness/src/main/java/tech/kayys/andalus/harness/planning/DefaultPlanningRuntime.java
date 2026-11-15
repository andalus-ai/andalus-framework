package tech.kayys.andalus.harness.planning;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default {@link PlanningRuntime} implementation.
 *
 * <h3>Algorithm</h3>
 * <ol>
 *   <li>Resolve the planner with the lexicographically smallest registered ID.</li>
 *   <li>Invoke {@link Planner#plan(PlanningRequest)} (or {@link Planner#revise}).</li>
 *   <li>If the planner returns a successful result, run {@link PlanValidator}.</li>
 *   <li>For revisions, require the same plan and execution identity and the next revision number.</li>
 *   <li>If validation fails, wrap into {@link PlanningResult#invalid(String)}.</li>
 *   <li>Any unexpected exception is caught and wrapped into a failed result.</li>
 * </ol>
 */
public class DefaultPlanningRuntime implements PlanningRuntime {

    private static final Logger LOG = Logger.getLogger(DefaultPlanningRuntime.class.getName());

    private final PlannerRegistry registry;
    private final PlanValidator validator;

    /**
     * Creates a runtime that uses all registered planners and applies validation.
     *
     * @param registry  planner registry (must have at least one planner registered before use)
     * @param validator plan validator applied after every successful plan creation
     */
    public DefaultPlanningRuntime(PlannerRegistry registry, PlanValidator validator) {
        if (registry == null) throw new IllegalArgumentException("registry must not be null");
        if (validator == null) throw new IllegalArgumentException("validator must not be null");
        this.registry = registry;
        this.validator = validator;
    }

    /** Convenience constructor using the {@link DefaultPlanValidator}. */
    public DefaultPlanningRuntime(PlannerRegistry registry) {
        this(registry, new DefaultPlanValidator());
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
                result -> validateRevisionIfSuccessful(result, request.currentPlan()));
    }

    private Planner resolveFirst() {
        return registry.all().stream().findFirst().orElse(null);
    }

    /**
     * Invokes the planner supplier, applies the supplied result validation, and handles errors.
     */
    private CompletionStage<PlanningResult> invokePlanner(
            java.util.function.Supplier<CompletionStage<PlanningResult>> supplier,
            java.util.function.Function<PlanningResult, PlanningResult> resultValidator) {

        try {
            return supplier.get().thenApply(resultValidator)
                    .exceptionally(ex -> {
                        LOG.log(Level.WARNING, "Planner threw unexpected exception", ex);
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
        return validatePlan(result.plan().orElseThrow());
    }

    private PlanningResult validateRevisionIfSuccessful(PlanningResult result, AgentPlan currentPlan) {
        if (result == null) {
            return PlanningResult.failed("Planner returned a null result.");
        }
        if (!result.isSuccess()) {
            return result;
        }

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
        return validatePlan(revisedPlan);
    }

    private PlanningResult validatePlan(AgentPlan plan) {
        PlanValidationResult validation = validator.validate(plan);
        if (!validation.valid()) {
            String reason = "Plan validation failed: " + String.join("; ", validation.errors());
            return PlanningResult.invalid(reason);
        }
        return PlanningResult.success(plan);
    }
}
