package tech.kayys.wayang.harness.environment.v3.resource;

import tech.kayys.wayang.harness.execution.state.ExecutionId;

/**
 * Universal runtime interface for discovering, leasing, and accounting environment resources.
 */
public interface ResourceRuntime {

    ResourceCatalog catalog();

    ResourceLease acquire(ExecutionId executionId, ResourceRequest request);

    ResourceUsage usage(ExecutionId executionId);
}
