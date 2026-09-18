package tech.kayys.wayang.harness.fabric;

import org.junit.jupiter.api.Test;
import tech.kayys.wayang.harness.consistency.state.ExecutionId;
import tech.kayys.wayang.harness.workflow.NodeId;

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
}
