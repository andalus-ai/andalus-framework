package tech.kayys.andalus.harness.planning;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Default {@link PlanValidator} implementation.
 *
 * <p>Applies structural checks:</p>
 * <ol>
 *   <li>Plan must have at least one step.</li>
 *   <li>All step IDs must be unique.</li>
 *   <li>All dependency references must point to existing step IDs.</li>
 *   <li>No cyclic dependencies among steps.</li>
 *   <li>All step descriptions must be non-blank.</li>
 * </ol>
 */
public class DefaultPlanValidator implements PlanValidator {

    @Override
    public PlanValidationResult validate(AgentPlan plan) {
        if (plan == null) {
            return PlanValidationResult.rejected("Plan must not be null.");
        }

        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        List<PlanStep> steps = plan.steps();

        if (steps.isEmpty()) {
            errors.add("Plan must contain at least one step.");
            return PlanValidationResult.rejected(errors, warnings);
        }

        // 1. Collect all step IDs and check uniqueness
        Set<String> stepIdValues = new HashSet<>();
        Set<String> duplicates = new HashSet<>();
        for (PlanStep step : steps) {
            String idVal = step.id().value();
            if (!stepIdValues.add(idVal)) {
                duplicates.add(idVal);
            }
        }
        if (!duplicates.isEmpty()) {
            errors.add("Duplicate step IDs found: " + duplicates);
        }

        // 2. Validate dependency references
        for (PlanStep step : steps) {
            for (PlanStepId dep : step.dependsOn()) {
                if (!stepIdValues.contains(dep.value())) {
                    errors.add("Step '" + step.id().value() + "' references unknown dependency '" + dep.value() + "'.");
                }
            }
        }

        // 3. Validate non-blank descriptions
        for (PlanStep step : steps) {
            if (step.description() == null || step.description().isBlank()) {
                errors.add("Step '" + step.id().value() + "' has a blank description.");
            }
        }


        // 4. Cycle detection (DFS)
        if (errors.isEmpty()) {
            List<String> cycleErrors = detectCycles(steps, stepIdValues);
            errors.addAll(cycleErrors);
        }

        // Warnings
        if (steps.size() > 50) {
            warnings.add("Plan has " + steps.size() + " steps; consider breaking into sub-plans.");
        }

        return errors.isEmpty()
                ? PlanValidationResult.acceptedWithWarnings(warnings)
                : PlanValidationResult.rejected(errors, warnings);
    }

    // ── cycle detection ───────────────────────────────────────────────────────

    private List<String> detectCycles(List<PlanStep> steps, Set<String> allIds) {
        // Build adjacency: stepId -> set of dependency stepIds
        java.util.Map<String, Set<String>> adj = new java.util.HashMap<>();
        for (PlanStep step : steps) {
            Set<String> deps = new HashSet<>();
            for (PlanStepId dep : step.dependsOn()) {
                deps.add(dep.value());
            }
            adj.put(step.id().value(), deps);
        }

        Set<String> visited = new HashSet<>();
        Set<String> inStack = new HashSet<>();
        List<String> errors = new ArrayList<>();

        for (String id : allIds) {
            if (!visited.contains(id)) {
                dfs(id, adj, visited, inStack, errors);
            }
        }
        return errors;
    }

    private void dfs(String node,
                     java.util.Map<String, Set<String>> adj,
                     Set<String> visited,
                     Set<String> inStack,
                     List<String> errors) {
        visited.add(node);
        inStack.add(node);

        for (String dep : adj.getOrDefault(node, Set.of())) {
            if (!visited.contains(dep)) {
                dfs(dep, adj, visited, inStack, errors);
            } else if (inStack.contains(dep)) {
                errors.add("Cyclic dependency detected involving step '" + node + "' -> '" + dep + "'.");
            }
        }
        inStack.remove(node);
    }
}
