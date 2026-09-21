package tech.kayys.andalus.harness.environment.v3.secret;

import java.time.Instant;

/**
 * Governed lease providing access to secret material.
 */
public interface SecretLease extends AutoCloseable {

    SecretRef ref();

    String reveal();

    Instant expiresAt();

    @Override
    void close();
}
