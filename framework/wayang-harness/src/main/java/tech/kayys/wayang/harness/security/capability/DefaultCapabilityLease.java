package tech.kayys.wayang.harness.security.capability;

import tech.kayys.wayang.harness.execution.state.ExecutionId;
import tech.kayys.wayang.harness.security.identity.Principal;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Default thread-safe implementation of {@link CapabilityLease}.
 */
public class DefaultCapabilityLease implements CapabilityLease {

    private final CapabilityLeaseId id;
    private final String capabilityId;
    private final Principal principal;
    private final ExecutionId executionId;
    private final CapabilityConstraints constraints;
    private final Instant expiresAt;
    private final AtomicBoolean revoked = new AtomicBoolean(false);

    public DefaultCapabilityLease(
            CapabilityLeaseId id,
            String capabilityId,
            Principal principal,
            ExecutionId executionId,
            CapabilityConstraints constraints,
            Instant expiresAt
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.capabilityId = Objects.requireNonNull(capabilityId, "capabilityId");
        this.principal = Objects.requireNonNull(principal, "principal");
        this.executionId = Objects.requireNonNull(executionId, "executionId");
        this.constraints = constraints != null ? constraints : CapabilityConstraints.unconstrained();
        this.expiresAt = expiresAt != null ? expiresAt : Instant.MAX;
    }

    @Override public CapabilityLeaseId id() { return id; }
    @Override public String capabilityId() { return capabilityId; }
    @Override public Principal principal() { return principal; }
    @Override public ExecutionId executionId() { return executionId; }
    @Override public CapabilityConstraints constraints() { return constraints; }
    @Override public Instant expiresAt() { return expiresAt; }

    @Override
    public boolean isValid() {
        return !revoked.get() && Instant.now().isBefore(expiresAt);
    }

    @Override
    public void revoke(String reason) {
        revoked.set(true);
    }
}
