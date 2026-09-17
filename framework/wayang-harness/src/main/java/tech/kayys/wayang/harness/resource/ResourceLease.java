package tech.kayys.wayang.harness.resource;

import java.time.Instant;

/**
 * Active lease granting an execution ownership and usage rights over a {@link HarnessResource}.
 */
public interface ResourceLease extends AutoCloseable {

    String id();

    HarnessResource resource();

    Instant acquiredAt();

    Instant expiresAt();

    boolean active();

    @Override
    void close();
}
