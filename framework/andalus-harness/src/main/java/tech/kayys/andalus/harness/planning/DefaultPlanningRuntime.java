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
 *   <li>Resolve the {@link Planner} from the {@link PlannerRegistry}. If none
 *       is available return {@link PlanningResult#failed(String)} immediately.</li>
 *   <li>Invoke {@link Planner#plan(PlanningRequest)} (or {@link Planner#revise}).</li>
 *   <li>If the planner returns a successful result, run {@link PlanValidator}.</li>
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

    // ── PlanningRuntime ───────────────────────────────────────────────────────

    @Override
    public CompletionStage<PlanningResult> createPlan(PlanningRequest request) {
        Planner planner = resolveFirst();
        if (planner == null) {
            return CompletableFuture.completedFuture(
                    PlanningResult.failed("No planner is registered."));
        }
        return invokePlanner(() -> planner.plan(request));
    }

    @Override
    public CompletionStage<PlanningResult> revisePlan(PlanRevisionRequest request) {
        Planner planner = resolveFirst();
        if (planner == null) {
            return CompletableFuture.completedFuture(
                    PlanningResult.failed("No planner is registered."));
        }
        return invokePlanner(() -> planner.revise(request));
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Planner resolveFirst() {
        return registry.all().stream().findFirst().orElse(null);
    }

    /**
     * Invokes the planner supplier, applies validation on success, and handles errors.
     */
    private CompletionStage<PlanningResult> invokePlanner(
            java.util.function.Supplier<CompletionStage<PlanningResult>> supplier) {

        try {
            return supplier.get().thenApply(this::validateIfSuccessful)
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
        if (!result.isSuccess()) {
            return result;
        }
        AgentPlan plan = result.plan().orElseThrow();
        PlanValidationResult vr = validator.validate(plan);
        if (!vr.valid()) {
            String reason = "Plan validation failed: " + String.join("; ", vr.errors());
            return PlanningResult.invalid(reason);
        }
        return result;
    }
}
