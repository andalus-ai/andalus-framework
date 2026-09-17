package tech.kayys.wayang.harness.resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceAllocatorTest {

    DefaultResourceAllocator allocator;

    @BeforeEach
    void setUp() {
        allocator = new DefaultResourceAllocator(new ResourceProviderRegistry());
    }

    @Test
    void testAcquireAndRelease() {
        ResourceRequest req = ResourceRequest.of(ResourceType.MEMORY, "mem-0");
        ResourceLease lease = allocator.acquire(req);

        assertNotNull(lease);
        assertNotNull(lease.id());
        assertTrue(lease.active());
        assertEquals(ResourceType.MEMORY, lease.resource().type());

        assertEquals(1, allocator.activeLeases().size());

        allocator.release(lease);
        assertEquals(0, allocator.activeLeases().size());
    }

    @Test
    void testMultipleLeases() {
        ResourceLease l1 = allocator.acquire(ResourceRequest.of(ResourceType.CPU, "cpu-1"));
        ResourceLease l2 = allocator.acquire(ResourceRequest.of(ResourceType.STORAGE, "stor-1"));

        assertEquals(2, allocator.activeLeases().size());

        allocator.release(l1);
        assertEquals(1, allocator.activeLeases().size());

        allocator.release(l2);
        assertEquals(0, allocator.activeLeases().size());
    }

    @Test
    void testLeaseAutoClose() {
        ResourceRequest req = ResourceRequest.of(ResourceType.WORKSPACE, "ws-0");
        ResourceLease lease = allocator.acquire(req);
        assertNotNull(lease);

        // Use try-with-resources simulation
        lease.close();
        assertFalse(lease.active());
    }
}
