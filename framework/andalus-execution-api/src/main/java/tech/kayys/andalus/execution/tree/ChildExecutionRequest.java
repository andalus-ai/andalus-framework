package tech.kayys.andalus.execution.tree;

import tech.kayys.andalus.communication.api.AgentRequest;
import tech.kayys.andalus.security.delegation.DelegationContext;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Request to create a delegated child execution.
 */
public record ChildExecutionRequest(
        AgentRequest request,
        DelegationContext delegation,
        Optional<Duration> timeout,
        Map<String, Object> attributes
) {

    public ChildExecutionRequest {
        Objects.requireNonNull(request, "request must not be null");

        delegation = Objects.requireNonNull(
                delegation,
                "delegation must not be null");

        timeout = timeout == null
                ? Optional.empty()
                : timeout;

        attributes = attributes == null
                ? Map.of()
                : Map.copyOf(attributes);
    }

    public static ChildExecutionRequest of(
            AgentRequest request,
            DelegationContext delegation) {

        return new ChildExecutionRequest(
                request,
                delegation,
                Optional.empty(),
                Map.of()
        );
    }
}
