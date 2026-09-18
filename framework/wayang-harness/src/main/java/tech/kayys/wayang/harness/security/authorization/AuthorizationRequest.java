package tech.kayys.wayang.harness.security.authorization;

import tech.kayys.wayang.harness.execution.state.ExecutionId;
import tech.kayys.wayang.harness.security.capability.CapabilityConstraints;
import tech.kayys.wayang.harness.security.identity.Principal;

import java.util.Objects;

/**
 * Request to authorize the use of a capability.
 */
public record AuthorizationRequest(
        Principal principal,
        ExecutionId executionId,
        String capabilityId,
        CapabilityConstraints requestedConstraints
) {
    public AuthorizationRequest {
        Objects.requireNonNull(principal, "principal");
        Objects.requireNonNull(executionId, "executionId");
        Objects.requireNonNull(capabilityId, "capabilityId");
        requestedConstraints = requestedConstraints != null ? requestedConstraints : CapabilityConstraints.unconstrained();
    }
}
