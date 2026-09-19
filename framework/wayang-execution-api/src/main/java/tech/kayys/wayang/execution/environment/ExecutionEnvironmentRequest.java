package tech.kayys.wayang.execution.environment;

import tech.kayys.wayang.execution.workspace.WorkspaceSpec;

import java.util.Map;
import java.util.Set;

/**
 * Request to provision an execution environment.
 */
public record ExecutionEnvironmentRequest(
        ExecutionEnvironmentId requestedId,
        EnvironmentType preferredType,
        EnvironmentCapabilities requiredCapabilities,
        WorkspaceSpec workspaceSpec,
        Set<ResourceRequirement> resourceRequirements,
        Map<String, Object> metadata
) {

    public ExecutionEnvironmentRequest {
        requestedId = requestedId != null ? requestedId : ExecutionEnvironmentId.generate();
        preferredType = preferredType != null ? preferredType : EnvironmentType.SANDBOX;
        requiredCapabilities = requiredCapabilities != null ? requiredCapabilities : EnvironmentCapabilities.defaults();
        workspaceSpec = workspaceSpec != null ? workspaceSpec : WorkspaceSpec.ephemeral();
        resourceRequirements = resourceRequirements != null ? Set.copyOf(resourceRequirements) : Set.of();
        metadata = metadata != null ? Map.copyOf(metadata) : Map.of();
    }
}
