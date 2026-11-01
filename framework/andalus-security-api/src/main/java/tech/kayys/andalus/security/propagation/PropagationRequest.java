package tech.kayys.andalus.security.propagation;

import tech.kayys.andalus.security.context.SecurityContext;
import tech.kayys.andalus.security.delegation.DelegationConstraints;

import java.util.Objects;

/**
 * Request to propagate security context to a downstream target agent.
 */
public record PropagationRequest(
        SecurityContext source,
        String targetAgentId,
        DelegationConstraints constraints
) {

    public PropagationRequest {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(targetAgentId, "targetAgentId");
        if (targetAgentId.isBlank()) {
            throw new IllegalArgumentException("targetAgentId must not be blank");
        }
        constraints = constraints == null ? DelegationConstraints.empty() : constraints;
    }

    /**
     * Creates a request with no delegated capabilities. Callers must explicitly
     * supply a constrained grant to delegate authority.
     */
    public static PropagationRequest of(SecurityContext source, String targetAgentId) {
        return new PropagationRequest(source, targetAgentId, DelegationConstraints.empty());
    }

    public static PropagationRequest of(SecurityContext source, String targetAgentId, DelegationConstraints constraints) {
        return new PropagationRequest(source, targetAgentId, constraints);
    }
}
