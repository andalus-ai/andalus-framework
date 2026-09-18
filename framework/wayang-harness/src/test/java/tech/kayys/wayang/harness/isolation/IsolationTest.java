package tech.kayys.wayang.harness.isolation;

import org.junit.jupiter.api.Test;
import tech.kayys.wayang.harness.semantics.*;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class IsolationTest {

    @Test
    void testResourceAllocationLeaseLifecycle() {
        EnvironmentId envId = EnvironmentId.of("sandbox-worker-1");
        ResourceRequirement cpuReq = ResourceRequirement.of(ResourceType.CPU, Quantity.cores(2));
        ResourceRequirement memReq = ResourceRequirement.of(ResourceType.MEMORY, Quantity.megabytes(2048));

        DefaultResourceAllocation alloc = DefaultResourceAllocation.allocate(
                envId,
                Set.of(cpuReq, memReq),
                Duration.ofMinutes(10)
        );

        assertEquals(LeaseState.ACTIVE, alloc.state());
        assertEquals(2, alloc.granted().size());
        assertNotNull(alloc.leaseId());

        // Renew lease
        ResourceAllocation renewed = alloc.renew(Duration.ofMinutes(5));
        assertTrue(renewed.expiresAt().isAfter(alloc.expiresAt()));
        assertEquals(LeaseState.ACTIVE, renewed.state());

        // Revoke lease
        ResourceAllocation revoked = renewed.revoke();
        assertEquals(LeaseState.REVOKING, revoked.state());

        // Release lease
        ResourceAllocation released = alloc.release();
        assertEquals(LeaseState.RELEASED, released.state());
    }

    @Test
    void testEnvironmentResolverSandboxing() {
        EnvironmentResolver resolver = new DefaultEnvironmentResolver();

        ExecutionEnvironment inProc = DefaultExecutionEnvironment.inProcess();
        ExecutionEnvironment sandbox = DefaultExecutionEnvironment.sandboxed(EnvironmentId.of("docker-1"));

        List<ExecutionEnvironment> candidates = List.of(inProc, sandbox);

        // Operation requiring sandbox
        Operation sandboxedOp = new DefaultOperation(
                OperationId.generate(),
                OperationType.EXECUTE,
                OperationIntent.of("compile code"),
                DefaultEffectDescriptor.builder().kind(EffectKind.EXECUTE).build(),
                OperationInput.empty(),
                ExecutionConstraints.sandboxed()
        );

        ResourceRequest req = ResourceRequest.of(Set.of(
                ResourceRequirement.of(ResourceType.CPU, Quantity.cores(2))
        ));

        EnvironmentResolution resolution = resolver.resolve(sandboxedOp, req, candidates);
        assertTrue(resolution.satisfied());
        assertEquals(EnvironmentType.SANDBOX, resolution.environment().orElseThrow().type());
        assertEquals(EnvironmentId.of("docker-1"), resolution.environment().orElseThrow().id());
        assertTrue(resolution.allocation().isPresent());
    }

    @Test
    void testEnvironmentResolverGpuRequirements() {
        EnvironmentResolver resolver = new DefaultEnvironmentResolver();

        ExecutionEnvironment cpuOnly = new DefaultExecutionEnvironment(
                EnvironmentId.of("cpu-worker"),
                EnvironmentType.LOCAL_PROCESS,
                EnvironmentCapabilities.defaults(),
                EnvironmentIsolation.unconstrained()
        );
        ExecutionEnvironment gpuWorker = new DefaultExecutionEnvironment(
                EnvironmentId.of("gpu-worker"),
                EnvironmentType.REMOTE_WORKER,
                EnvironmentCapabilities.gpuAccelerated(),
                EnvironmentIsolation.unconstrained()
        );

        List<ExecutionEnvironment> candidates = List.of(cpuOnly, gpuWorker);

        Operation mlOp = DefaultOperation.of(
                OperationType.COMPUTE,
                OperationIntent.of("Inference"),
                DefaultEffectDescriptor.builder().kind(EffectKind.PURE).build()
        );

        ResourceRequest gpuReq = ResourceRequest.of(Set.of(
                ResourceRequirement.of(ResourceType.GPU, Quantity.of(1, "card"))
        ));

        EnvironmentResolution resolution = resolver.resolve(mlOp, gpuReq, candidates);
        assertTrue(resolution.satisfied());
        assertEquals(EnvironmentId.of("gpu-worker"), resolution.environment().orElseThrow().id());
    }
}
