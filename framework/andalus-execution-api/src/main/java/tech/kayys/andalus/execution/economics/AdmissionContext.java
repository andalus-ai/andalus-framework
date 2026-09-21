package tech.kayys.andalus.execution.economics;

import tech.kayys.andalus.execution.mobility.ExecutionIdentity;
import tech.kayys.andalus.execution.resource.model.ResourceScope;
import tech.kayys.andalus.execution.resource.requirement.ResourceRequirement;

import java.util.Objects;
import java.util.Set;

/**
 * Context evaluated by the admission controller.
 */
public record AdmissionContext(
    ExecutionIdentity executionId,
    ResourceScope scope,
    Set<ResourceRequirement> requirements,
    ResourceBudget budget
) {
    public AdmissionContext {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(scope, "scope cannot be null");
        requirements = requirements != null ? Set.copyOf(requirements) : Set.of();
    }

    public static AdmissionContext of(ExecutionIdentity executionId, ResourceScope scope, Set<ResourceRequirement> requirements) {
        return new AdmissionContext(executionId, scope, requirements, null);
    }
}
