package tech.kayys.wayang.harness.environment.v3.resource;

import tech.kayys.wayang.harness.execution.state.ExecutionId;

import java.time.Instant;

/**
 * Governed lease for an allocated environmental resource.
 */
public interface ResourceLease extends AutoCloseable {

    LeaseId id();

    ResourceId resourceId();

    ExecutionId executionId();

    LeaseConstraints constraints();

    Instant expiresAt();

    boolean isExpired();

    @Override
    void close();
}
