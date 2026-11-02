package tech.kayys.andalus.harness.fabric;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.harness.consistency.state.ExecutionId;
import tech.kayys.andalus.workflow.graph.NodeId;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FabricTest {

    @Test
    void testWorkerRegistrationAndHeartbeat() {
        WorkerFabric fabric = new DefaultWorkerFabric();
        WorkerId workerId = WorkerId.of("worker-1");
        ExecutionWorker worker = DefaultExecutionWorker.create(workerId);

        fabric.register(worker);

        ExecutionWorker retrieved = fabric.get(workerId).orElseThrow();
        assertEquals(workerId, retrieved.id());

        // Send heartbeat
        WorkerHeartbeat heartbeat = WorkerHeartbeat.of(workerId, WorkerStatus.BUSY, WorkerResources.of(8, 16384));
        fabric.heartbeat(heartbeat);

        ExecutionWorker updated = fabric.get(workerId).orElseThrow();
        assertEquals(WorkerStatus.BUSY, updated.status());
    }

    @Test
    void testAssignmentDispatchAndFencing() {
        WorkerFabric fabric = new DefaultWorkerFabric();
        WorkerId workerId = WorkerId.of("worker-2");
        ExecutionWorker worker = DefaultExecutionWorker.create(workerId).withStatus(WorkerStatus.READY);
        fabric.register(worker);

        ExecutionId execId = ExecutionId.of("exec-1");
        NodeId nodeId = NodeId.of("node-a");

        ExecutionAssignment assignment = fabric.assign(execId, nodeId, workerId, AssignmentConstraints.defaults());
        assertNotNull(assignment);
        assertEquals(workerId, assignment.workerId());

        // Validate fence
        assertTrue(fabric.validateFence(assignment.fence()));

        // Create stale fence
        AssignmentFence staleFence = AssignmentFence.of(assignment.id(), assignment.fence().generation() - 1);
        assertFalse(fabric.validateFence(staleFence));
    }
    @Test
    void capabilityAwarePlacementChecksRuntimeAndAvailableResources() {
        WorkerFabric fabric = new DefaultWorkerFabric();
        WorkerId workerId = WorkerId.of("worker-capacity");
        fabric.register(DefaultExecutionWorker.create(workerId).withStatus(WorkerStatus.READY));

        assertEquals(1, fabric.findCapable(new WorkerRequirements(
                "sandbox", "generic", false, 2, 1024)).size());
        assertTrue(fabric.findCapable(new WorkerRequirements(
                "container", "cuda", true, 2, 1024)).isEmpty());
        assertTrue(fabric.findCapable(new WorkerRequirements(
                "sandbox", "generic", false, 8, 1024)).isEmpty());
    }

    @Test
    void gracefulDrainStopsNewAssignmentsAndWaitsForActiveOnes() {
        DefaultWorkerFabric fabric = new DefaultWorkerFabric();
        WorkerId workerId = WorkerId.of("worker-drain");
        fabric.register(DefaultExecutionWorker.create(workerId).withStatus(WorkerStatus.READY));
        ExecutionAssignment assignment = fabric.assign(
                ExecutionId.of("exec-drain"), NodeId.of("node-a"), workerId,
                AssignmentConstraints.defaults());

        WorkerDrainResult draining = fabric.beginDrain(workerId);
        assertTrue(draining.draining());
        assertFalse(draining.drained());
        assertEquals(1, draining.activeAssignments());

        assertThrows(IllegalStateException.class, () -> fabric.assign(
                ExecutionId.of("exec-drain-2"), NodeId.of("node-b"), workerId,
                AssignmentConstraints.defaults()));
        assertTrue(fabric.completeAssignment(assignment.fence()));

        WorkerDrainResult drained = fabric.drainStatus(workerId);
        assertTrue(drained.drained());
        assertEquals(0, drained.activeAssignments());
        assertDoesNotThrow(() -> fabric.unregister(workerId));
    }

    @Test
    void rejectsUnregisterWhileAssignmentsAreActive() {
        DefaultWorkerFabric fabric = new DefaultWorkerFabric();
        WorkerId workerId = WorkerId.of("worker-active");
        fabric.register(DefaultExecutionWorker.create(workerId).withStatus(WorkerStatus.READY));
        fabric.assign(ExecutionId.of("exec-active"), NodeId.of("node-a"), workerId,
                AssignmentConstraints.defaults());

        assertThrows(IllegalStateException.class, () -> fabric.unregister(workerId));
    }

    @Test
    void ignoresOutOfOrderHeartbeats() {
        DefaultWorkerFabric fabric = new DefaultWorkerFabric();
        WorkerId workerId = WorkerId.of("worker-heartbeat");
        fabric.register(DefaultExecutionWorker.create(workerId).withStatus(WorkerStatus.READY));
        fabric.heartbeat(new WorkerHeartbeat(workerId,
                java.time.Instant.parse("2000-01-01T00:00:00Z"),
                WorkerStatus.OFFLINE, WorkerResources.of(1, 256),
                WorkerHealth.unhealthy("stale")));
        assertEquals(WorkerStatus.READY, fabric.get(workerId).orElseThrow().status());
    }
}
