package tech.kayys.wayang.harness.resource;

/**
 * Provider SPI capable of acquiring and managing leases for specific resource types.
 */
public interface ResourceProvider {

    ResourceType type();

    boolean supports(ResourceRequest request);

    ResourceLease acquire(ResourceRequest request);
}
