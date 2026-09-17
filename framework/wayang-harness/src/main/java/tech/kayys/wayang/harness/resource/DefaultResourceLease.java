package tech.kayys.wayang.harness.resource;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Standard implementation of {@link ResourceLease} with atomic close mechanics.
 */
public class DefaultResourceLease implements ResourceLease {

    private final String id;
    private final HarnessResource resource;
    private final Instant acquiredAt;
    private final Instant expiresAt;
    private final Runnable onClose;
    private final AtomicBoolean closed = new AtomicBoolean(false);

    public DefaultResourceLease(HarnessResource resource, Instant expiresAt, Runnable onClose) {
        this.id = "lease-" + UUID.randomUUID();
        this.resource = Objects.requireNonNull(resource, "resource");
        this.acquiredAt = Instant.now();
        this.expiresAt = expiresAt != null ? expiresAt : Instant.now().plusSeconds(3600);
        this.onClose = onClose != null ? onClose : () -> {};
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public HarnessResource resource() {
        return resource;
    }

    @Override
    public Instant acquiredAt() {
        return acquiredAt;
    }

    @Override
    public Instant expiresAt() {
        return expiresAt;
    }

    @Override
    public boolean active() {
        return !closed.get() && Instant.now().isBefore(expiresAt);
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            onClose.run();
        }
    }
}
