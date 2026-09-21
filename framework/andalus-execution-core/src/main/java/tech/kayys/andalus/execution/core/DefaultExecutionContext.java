package tech.kayys.andalus.execution.core;

import tech.kayys.andalus.execution.CancellationToken;
import tech.kayys.andalus.execution.ExecutionContext;
import tech.kayys.andalus.execution.ExecutionLifecycle;
import tech.kayys.andalus.execution.ExecutionMetadata;
import tech.kayys.andalus.security.propagation.SecurityContextSnapshot;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Thread-safe mutable implementation of {@link ExecutionContext}.
 */
public final class DefaultExecutionContext implements ExecutionContext {

    private final ExecutionMetadata metadata;
    private final CancellationToken cancellation;
    private final SecurityContextSnapshot security;
    private final AtomicReference<ExecutionLifecycle> lifecycle;

    public DefaultExecutionContext(
            ExecutionMetadata metadata,
            CancellationToken cancellation,
            SecurityContextSnapshot security,
            ExecutionLifecycle initialLifecycle) {
        this.metadata = Objects.requireNonNull(metadata, "metadata");
        this.cancellation = Objects.requireNonNull(cancellation, "cancellation");
        this.security = Objects.requireNonNull(security, "security");
        this.lifecycle = new AtomicReference<>(
                initialLifecycle == null ? ExecutionLifecycle.CREATED : initialLifecycle
        );
    }

    @Override
    public ExecutionMetadata metadata() {
        return metadata;
    }

    @Override
    public CancellationToken cancellation() {
        return cancellation;
    }

    @Override
    public SecurityContextSnapshot security() {
        return security;
    }

    @Override
    public ExecutionLifecycle lifecycle() {
        return lifecycle.get();
    }

    @Override
    public void transition(ExecutionLifecycle next) {
        Objects.requireNonNull(next, "next lifecycle state must not be null");
        lifecycle.set(next);
    }
}
