package tech.kayys.andalus.a2a.execution;

import tech.kayys.andalus.communication.api.AgentRequest;
import tech.kayys.andalus.security.propagation.SecurityContextSnapshot;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Protocol-specific request passed from the A2A endpoint to the execution bridge (6.16.1.3).
 *
 * <p>The optional {@code callerExecutionId} is populated when the request originates
 * from a durable Andalus Claw execution.</p>
 */
public record A2AExecutionRequest(
        String taskId,
        AgentRequest request,
        SecurityContextSnapshot security,
        Map<String, Object> protocolMetadata,
        Optional<String> callerExecutionId
) {

    public A2AExecutionRequest {
        Objects.requireNonNull(taskId, "taskId");
        Objects.requireNonNull(request, "request");
        Objects.requireNonNull(security, "security");
        protocolMetadata = protocolMetadata == null ? Map.of() : Map.copyOf(protocolMetadata);
        callerExecutionId = callerExecutionId == null ? Optional.empty() : callerExecutionId;
    }

    /**
     * Backward-compatible constructor for non-durable callers.
     */
    public A2AExecutionRequest(
            String taskId,
            AgentRequest request,
            SecurityContextSnapshot security,
            Map<String, Object> protocolMetadata
    ) {
        this(taskId, request, security, protocolMetadata, Optional.empty());
    }

    public static A2AExecutionRequest of(String taskId, AgentRequest request, SecurityContextSnapshot security) {
        return new A2AExecutionRequest(taskId, request, security, Map.of(), Optional.empty());
    }

    public static A2AExecutionRequest of(String taskId, AgentRequest request, SecurityContextSnapshot security, String callerExecutionId) {
        return new A2AExecutionRequest(taskId, request, security, Map.of(), Optional.ofNullable(callerExecutionId));
    }

    public boolean isDurable() {
        return callerExecutionId.isPresent();
    }
}
