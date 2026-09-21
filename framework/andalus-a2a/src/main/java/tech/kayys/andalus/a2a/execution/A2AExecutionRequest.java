package tech.kayys.andalus.a2a.execution;

import tech.kayys.andalus.communication.api.AgentRequest;
import tech.kayys.andalus.security.propagation.SecurityContextSnapshot;

import java.util.Map;
import java.util.Objects;

/**
 * Protocol-specific request passed from the A2A endpoint to the execution bridge.
 */
public record A2AExecutionRequest(
        String taskId,
        AgentRequest request,
        SecurityContextSnapshot security,
        Map<String, Object> protocolMetadata
) {

    public A2AExecutionRequest {
        Objects.requireNonNull(taskId, "taskId");
        Objects.requireNonNull(request, "request");
        Objects.requireNonNull(security, "security");
        protocolMetadata = protocolMetadata == null ? Map.of() : Map.copyOf(protocolMetadata);
    }

    public static A2AExecutionRequest of(String taskId, AgentRequest request, SecurityContextSnapshot security) {
        return new A2AExecutionRequest(taskId, request, security, Map.of());
    }
}
