package tech.kayys.andalus.harness.planning;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Derives a deterministic replanning assessment from a plan and its observed
 * execution feedback. This type is analytical only: it does not execute,
 * mutate, or authorize plan steps.
 */
public final class PlanReplanningAnalyzer {

    private final PlanDependencyResolver dependencyResolver;

    public PlanReplanningAnalyzer() {
        this(new PlanDependencyResolver());
    }

    public PlanReplanningAnalyzer(PlanDependencyResolver dependencyResolver) {
        if (dependencyResolver == null) {
            throw new IllegalArgumentException("dependencyResolver must not be null.");
        }
        this.dependencyResolver = dependencyResolver;
    }

    /**
     * Analyzes terminal observations for the supplied plan.
     *
     * <p>FAILED and BLOCKED steps are direct retry candidates. Their transitive
     * dependents are dependency-affected unless those dependents were already
     * observed as SUCCEEDED or SKIPPED. Successful and skipped steps are never
     * automatically selected for replanning. Steps without feedback remain
     * unobserved unless dependency propagation makes them replanning candidates.</p>
     *
     * @throws IllegalArgumentException for an invalid plan, null feedback entry,
     *         duplicate feedback for a step, or feedback for a step outside the plan
     */
    public PlanReplanningAssessment analyze(
            AgentPlan plan,
            List<PlanExecutionFeedback> feedback
    ) {
        // Reuse the canonical graph validation so malformed dependencies and
        // cycles have exactly the same semantics as execution ordering.
        dependencyResolver.resolve(plan);

        Map<PlanStepId, PlanStep> stepsById = new LinkedHashMap<>();
        Map<PlanStepId, List<PlanStepId>> dependents = new HashMap<>();
        for (PlanStep step : plan.steps()) {
            stepsById.put(step.id(), step);
            for (PlanStepId dependencyId : new LinkedHashSet<>(step.dependsOn())) {
                dependents.computeIfAbsent(dependencyId, ignored -> new ArrayList<>())
                        .add(step.id());
            }
        }

        Map<PlanStepId, PlanExecutionFeedback.Outcome> outcomes = new HashMap<>();
        if (feedback != null) {
            for (int i = 0; i < feedback.size(); i++) {
                PlanExecutionFeedback observation = feedback.get(i);
                if (observation == null) {
                    throw new IllegalArgumentException("Feedback contains a null entry at index " + i + ".");
                }
                if (!stepsById.containsKey(observation.stepId())) {
                    throw new IllegalArgumentException("Feedback references unknown step '"
                            + observation.stepId().value() + "'.");
                }
                if (outcomes.putIfAbsent(observation.stepId(), observation.outcome()) != null) {
                    throw new IllegalArgumentException("Duplicate feedback for step '"
                            + observation.stepId().value() + "'.");
                }
            }
        }

        Set<PlanStepId> completed = new HashSet<>();
        Set<PlanStepId> directRetry = new HashSet<>();
        Set<PlanStepId> skipped = new HashSet<>();
        Set<PlanStepId> unobserved = new HashSet<>();

        for (PlanStepId stepId : stepsById.keySet()) {
            PlanExecutionFeedback.Outcome outcome = outcomes.get(stepId);
            if (outcome == null) {
                unobserved.add(stepId);
                continue;
            }
            switch (outcome) {
                case SUCCEEDED -> completed.add(stepId);
                case FAILED, BLOCKED -> directRetry.add(stepId);
                case SKIPPED -> skipped.add(stepId);
            }
        }

        Set<PlanStepId> dependencyAffected = new HashSet<>();
        Set<PlanStepId> visited = new HashSet<>();
        Queue<PlanStepId> pending = new ArrayDeque<>(directRetry);

        while (!pending.isEmpty()) {
            PlanStepId failedOrBlockedId = pending.remove();
            if (!visited.add(failedOrBlockedId)) {
                continue;
            }
            for (PlanStepId dependentId : dependents.getOrDefault(failedOrBlockedId, List.of())) {
                PlanExecutionFeedback.Outcome dependentOutcome = outcomes.get(dependentId);
                if (dependentOutcome == PlanExecutionFeedback.Outcome.SUCCEEDED
                        || dependentOutcome == PlanExecutionFeedback.Outcome.SKIPPED) {
                    // An explicit terminal observation takes precedence over
                    // graph-based inference; do not retry or propagate through it.
                    continue;
                }
                if (!directRetry.contains(dependentId)) {
                    dependencyAffected.add(dependentId);
                }
                pending.add(dependentId);
            }
        }

        List<PlanStepId> orderedCompleted = inPlanOrder(stepsById, completed);
        List<PlanStepId> orderedDirectRetry = inPlanOrder(stepsById, directRetry);
        List<PlanStepId> orderedDependencyAffected = inPlanOrder(stepsById, dependencyAffected);
        List<PlanStepId> orderedSkipped = inPlanOrder(stepsById, skipped);
        List<PlanStepId> orderedUnobserved = inPlanOrder(stepsById, unobserved);
        Set<PlanStepId> retryUnion = new HashSet<>(directRetry);
        retryUnion.addAll(dependencyAffected);

        return new PlanReplanningAssessment(
                orderedCompleted,
                orderedDirectRetry,
                orderedDependencyAffected,
                orderedSkipped,
                orderedUnobserved,
                inPlanOrder(stepsById, retryUnion));
    }

    private static List<PlanStepId> inPlanOrder(
            Map<PlanStepId, PlanStep> stepsById,
            Set<PlanStepId> selected
    ) {
        return stepsById.keySet().stream().filter(selected::contains).toList();
    }
}
