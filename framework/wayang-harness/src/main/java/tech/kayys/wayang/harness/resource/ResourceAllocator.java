package tech.kayys.wayang.harness.resource;

/**
 * SPI for acquiring and releasing environmental resource leases.
 */
public interface ResourceAllocator {

    ResourceLease acquire(ResourceRequest request);

    void release(ResourceLease lease);
}
