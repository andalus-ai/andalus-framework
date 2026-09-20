package tech.kayys.wayang.execution.economics;

import tech.kayys.wayang.execution.mobility.ExecutionIdentity;
import tech.kayys.wayang.execution.resource.model.ResourceScope;
import tech.kayys.wayang.execution.resource.requirement.ResourceRequirement;

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
