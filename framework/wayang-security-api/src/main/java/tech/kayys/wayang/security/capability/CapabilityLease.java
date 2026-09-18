package tech.kayys.wayang.security.capability;

import tech.kayys.wayang.security.identity.Principal;

import java.time.Instant;

/**
 * Authority lease granting permission to exercise a capability under defined constraints and time window.
 */
public interface CapabilityLease extends AutoCloseable {

    CapabilityLeaseId id();

    String capabilityId();

    Principal principal();

    String executionId();

    CapabilityConstraints constraints();

    Instant expiresAt();

    boolean isValid();

    void revoke(String reason);

    @Override
    default void close() {
        revoke("Closed");
    }
}
