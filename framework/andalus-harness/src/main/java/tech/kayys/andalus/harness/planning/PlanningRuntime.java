package tech.kayys.andalus.harness.planning;

import java.util.concurrent.CompletionStage;

/**
 * Runtime contract for managing the lifecycle of agent plans.
 *
 * <p>The {@code PlanningRuntime} acts as the orchestration layer between the
 * caller and one or more {@link Planner} implementations. It is responsible
 * for:</p>
 * <ul>
 *   <li>Selecting the appropriate planner (by ID or default).</li>
 *   <li>Delegating plan creation and revision to the chosen planner.</li>
 *   <li>Running the {@link PlanValidator} after a plan is produced.</li>
 *   <li>Returning a {@link PlanningResult} that reflects both planner
 *       output and validation outcome.</li>
 * </ul>
 *
 * <p>The runtime never executes plan steps — execution is owned by the
 * agent's turn executor pipeline.</p>
 */
public interface PlanningRuntime {

    /**
     * Creates a new plan for the given request.
     *
     * @param request the planning request
     * @return a {@link CompletionStage} resolving to a {@link PlanningResult}
     */
    CompletionStage<PlanningResult> createPlan(PlanningRequest request);

    /**
     * Revises an existing plan based on new context or observations.
     *
     * @param request the revision request
     * @return a {@link CompletionStage} resolving to a {@link PlanningResult}
     */
    CompletionStage<PlanningResult> revisePlan(PlanRevisionRequest request);
}
