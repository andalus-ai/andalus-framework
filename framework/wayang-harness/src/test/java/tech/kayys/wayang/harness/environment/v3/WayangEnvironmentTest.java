package tech.kayys.wayang.harness.environment.v3;

import org.junit.jupiter.api.Test;
import tech.kayys.wayang.harness.environment.v3.resource.DefaultResourceCatalog;
import tech.kayys.wayang.harness.environment.v3.resource.DefaultResourceRuntime;
import tech.kayys.wayang.harness.environment.v3.resource.LeaseId;
import tech.kayys.wayang.harness.environment.v3.resource.ResourceAccessMode;
import tech.kayys.wayang.harness.environment.v3.resource.ResourceDescriptor;
import tech.kayys.wayang.harness.environment.v3.resource.ResourceId;
import tech.kayys.wayang.harness.environment.v3.resource.ResourceLease;
import tech.kayys.wayang.harness.environment.v3.resource.ResourceRequest;
import tech.kayys.wayang.harness.environment.v3.resource.ResourceRequirements;
import tech.kayys.wayang.harness.environment.v3.resource.ResourceType;
import tech.kayys.wayang.harness.environment.v3.resource.ResourceUsage;
import tech.kayys.wayang.harness.execution.state.ExecutionId;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class WayangEnvironmentTest {

    @Test
    void testEnvironmentCreationAndSnapshot() {
        DefaultWayangEnvironment env = DefaultWayangEnvironment.local();
        assertNotNull(env.id());
        assertEquals(EnvironmentState.READY, env.state());
        assertNotNull(env.descriptor());
        assertNotNull(env.capabilities());

        DefaultEnvironmentRuntime runtime = new DefaultEnvironmentRuntime(env);
        EnvironmentSnapshot snapshot = runtime.snapshot();
        assertNotNull(snapshot);
        assertEquals(env.descriptor().id(), snapshot.descriptor().id());
        assertTrue(runtime.capabilities().allows("filesystem.read"));
    }

    @Test
    void testResourceCatalogAndRuntimeLease() {
        DefaultResourceCatalog catalog = new DefaultResourceCatalog();
        ResourceId gpuId = ResourceId.of("gpu-nvidia-a100");
        ResourceDescriptor gpuDesc = new ResourceDescriptor(gpuId, "A100", ResourceType.GPU, Map.of("vram", "80GB"), 1L);
        catalog.register(gpuDesc);

        Collection<ResourceDescriptor> gpus = catalog.findByType(ResourceType.GPU);
        assertEquals(1, gpus.size());
        assertEquals("A100", gpus.iterator().next().name());

        DefaultResourceRuntime resourceRuntime = new DefaultResourceRuntime(catalog);
        ExecutionId execId = ExecutionId.of("exec-101");

        ResourceRequest req = ResourceRequest.of(ResourceType.GPU, ResourceRequirements.of(1L), ResourceAccessMode.EXCLUSIVE);
        try (ResourceLease lease = resourceRuntime.acquire(execId, req)) {
            assertNotNull(lease.id());
            assertEquals(gpuId, lease.resourceId());
            assertEquals(execId, lease.executionId());
            assertFalse(lease.isExpired());
        }

        ResourceUsage usage = new ResourceUsage(execId, Duration.ofMinutes(2), 1024L * 1024L * 500L, 0L, 0L, Map.of("gpu-time", 120L));
        resourceRuntime.recordUsage(execId, usage);

        ResourceUsage recorded = resourceRuntime.usage(execId);
        assertEquals(Duration.ofMinutes(2), recorded.cpuTime());
        assertEquals(120L, recorded.customMetrics().get("gpu-time"));
    }

    @Test
    void testMissingResourceThrows() {
        DefaultResourceCatalog catalog = new DefaultResourceCatalog();
        DefaultResourceRuntime runtime = new DefaultResourceRuntime(catalog);

        assertThrows(NoSuchElementException.class, () ->
                runtime.acquire(ExecutionId.of("e1"), ResourceRequest.of(ResourceType.NETWORK))
        );
    }
}
