package tech.kayys.wayang.harness.fabric;

import tech.kayys.wayang.harness.consistency.state.ExecutionId;
import tech.kayys.wayang.harness.workflow.NodeId;

import java.util.List;
import java.util.Optional;

/**
 * Worker fabric coordinator managing worker registrations, heartbeats, and assignment dispatching.
 */
public interface WorkerFabric {

    void register(ExecutionWorker worker);

    void unregister(WorkerId workerId);

    void heartbeat(WorkerHeartbeat heartbeat);

    Optional<ExecutionWorker> get(WorkerId workerId);

    List<ExecutionWorker> findCapable(WorkerCapabilities requirements);

    ExecutionAssignment assign(ExecutionId executionId, NodeId nodeId, WorkerId workerId, AssignmentConstraints constraints);

    boolean validateFence(AssignmentFence fence);
}
