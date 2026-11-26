package tech.kayys.andalus.harness.planning;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Resolves a plan's dependency graph into a deterministic execution order.
 *
 * <p>Steps that are simultaneously ready retain their declaration order.
 * This class only resolves dependencies; it does not execute steps or decide
 * whether their actions are authorized.</p>
 */
public final class PlanDependencyResolver {

    /**
     * Returns an immutable topological ordering of the plan's steps.
     *
     * @throws IllegalArgumentException if the plan is null, contains null or
     *         duplicate steps, references an unknown dependency, or contains
     *         a dependency cycle
     */
    public List<PlanStep> resolve(AgentPlan plan) {
        if (plan == null) {
            throw new IllegalArgumentException("Plan must not be null.");
        }

        List<PlanStep> steps = plan.steps();
        Map<String, Integer> declarationOrder = new HashMap<>();
        Map<String, PlanStep> stepsById = new HashMap<>();

        for (int i = 0; i < steps.size(); i++) {
            PlanStep step = steps.get(i);
            if (step == null) {
                throw new IllegalArgumentException("Plan contains a null step at index " + i + ".");
            }
            String id = step.id().value();
            if (declarationOrder.putIfAbsent(id, i) != null) {
                throw new IllegalArgumentException("Duplicate step ID: '" + id + "'.");
            }
            stepsById.put(id, step);
        }

        Map<String, Integer> remainingDependencies = new HashMap<>();
        Map<String, List<String>> dependents = new HashMap<>();

        for (PlanStep step : steps) {
            String stepId = step.id().value();
            Set<String> uniqueDependencies = new HashSet<>();
            for (PlanStepId dependency : step.dependsOn()) {
                if (dependency == null) {
                    throw new IllegalArgumentException(
                            "Step '" + stepId + "' contains a null dependency reference.");
                }
                String dependencyId = dependency.value();
                if (!stepsById.containsKey(dependencyId)) {
                    throw new IllegalArgumentException("Step '" + stepId
                            + "' references unknown dependency '" + dependencyId + "'.");
                }
                if (uniqueDependencies.add(dependencyId)) {
                    dependents.computeIfAbsent(dependencyId, ignored -> new ArrayList<>())
                            .add(stepId);
                }
            }
            remainingDependencies.put(stepId, uniqueDependencies.size());
        }

        PriorityQueue<String> ready = new PriorityQueue<>(
                Comparator.comparingInt(declarationOrder::get));
        for (PlanStep step : steps) {
            String id = step.id().value();
            if (remainingDependencies.get(id) == 0) {
                ready.add(id);
            }
        }

        List<PlanStep> ordered = new ArrayList<>(steps.size());
        while (!ready.isEmpty()) {
            String completedId = ready.remove();
            ordered.add(stepsById.get(completedId));

            for (String dependentId : dependents.getOrDefault(completedId, List.of())) {
                int remaining = remainingDependencies.compute(dependentId,
                        (ignored, count) -> count - 1);
                if (remaining == 0) {
                    ready.add(dependentId);
                }
            }
        }

        if (ordered.size() != steps.size()) {
            List<String> blocked = steps.stream()
                    .map(step -> step.id().value())
                    .filter(id -> remainingDependencies.get(id) > 0)
                    .toList();
            throw new IllegalArgumentException(
                    "Plan contains a dependency cycle; unresolved steps: " + blocked + ".");
        }

        return List.copyOf(ordered);
    }
}
