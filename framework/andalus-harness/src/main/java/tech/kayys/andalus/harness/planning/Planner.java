package tech.kayys.andalus.harness.planning;

import java.util.concurrent.CompletionStage;

/**
 * SPI for planning engines that propose a sequence of steps toward a goal.
 *
 * <p>A {@code Planner} is responsible for <em>proposing</em> a plan; it never
 * executes steps directly. The {@link PlanningRuntime} coordinates planners,
 * applies validation, and manages plan lifecycle.</p>
 *
 * <h3>Contract</h3>
 * <ul>
 *   <li>{@link #id()} must return a stable, non-blank identifier unique among
 *       registered planners.</li>
 *   <li>{@link #plan(PlanningRequest)} must complete exceptionally only for
 *       truly unexpected errors; ordinary failures (no viable plan) should be
 *       expressed as {@link PlanningResult#failed(String)}.</li>
 *   <li>The default {@link #revise(PlanRevisionRequest)} implementation re-plans
 *       from scratch using the revision request goal/context; override for
 *       incremental revision strategies.</li>
 * </ul>
 */
public interface Planner {

    /**
     * Stable identifier for this planner (e.g. {@code "hierarchical-task-network"}).
     */
    String id();

    /**
     * Produces a new {@link AgentPlan} for the given request.
     *
     * @param request the planning request containing goal, context, and constraints
     * @return a {@link CompletionStage} that resolves to a {@link PlanningResult}
     */
    CompletionStage<PlanningResult> plan(PlanningRequest request);

    /**
     * Revises an existing plan in light of new information.
     *
     * <p>The default implementation discards the current plan and calls
     * {@link #plan(PlanningRequest)} with a synthesized request derived from
     * the revision context. Planners that support true incremental revision
     * should override this method.</p>
     *
     * @param request the revision request
     * @return a {@link CompletionStage} that resolves to a {@link PlanningResult}
     */
    default CompletionStage<PlanningResult> revise(PlanRevisionRequest request) {
        // Default: discard the old plan and replan from scratch using the revised context.
        PlanningRequest synth = PlanningRequest.of(request.context());
        return plan(synth);
    }
}
