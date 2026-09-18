package tech.kayys.wayang.harness.environment.v3.resource;

import tech.kayys.wayang.harness.execution.state.ExecutionId;

import java.time.Instant;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Default implementation of {@link ResourceLease}.
 */
public class DefaultResourceLease implements ResourceLease {

    private final LeaseId id;
    private final ResourceId resourceId;
    private final ExecutionId executionId;
    private final LeaseConstraints constraints;
    private final Instant expiresAt;
    private final Consumer<LeaseId> onClose;
    private volatile boolean closed = false;

    public DefaultResourceLease(
            LeaseId id,
            ResourceId resourceId,
            ExecutionId executionId,
            LeaseConstraints constraints,
            Instant expiresAt,
            Consumer<LeaseId> onClose
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.resourceId = Objects.requireNonNull(resourceId, "resourceId");
        this.executionId = Objects.requireNonNull(executionId, "executionId");
        this.constraints = constraints != null ? constraints : LeaseConstraints.unconstrained();
        this.expiresAt = expiresAt != null ? expiresAt : Instant.MAX;
        this.onClose = onClose;
    }

    @Override
    public LeaseId id() {
        return id;
    }

    @Override
    public ResourceId resourceId() {
        return resourceId;
    }

    @Override
    public ExecutionId executionId() {
        return executionId;
    }

    @Override
    public LeaseConstraints constraints() {
        return constraints;
    }

    @Override
    public Instant expiresAt() {
        return expiresAt;
    }

    @Override
    public boolean isExpired() {
        return closed || Instant.now().isAfter(expiresAt);
    }

    @Override
    public void close() {
        if (!closed) {
            closed = true;
            if (onClose != null) {
                onClose.accept(id);
            }
        }
    }
}
