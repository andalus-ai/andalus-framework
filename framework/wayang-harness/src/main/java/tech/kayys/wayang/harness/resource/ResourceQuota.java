package tech.kayys.wayang.harness.resource;

/**
 * Capacity and usage governor tracking resource boundaries for agent executions.
 */
public interface ResourceQuota {

    QuotaLimit limit(ResourceType type);

    QuotaUsage usage(ResourceType type);

    boolean canAllocate(ResourceRequest request);
}
