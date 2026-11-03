package tech.kayys.andalus.harness.fabric;

import tech.kayys.andalus.harness.consistency.state.ExecutionId;
import tech.kayys.andalus.workflow.graph.NodeId;

import java.util.List;
import java.util.Optional;

/** Worker membership, capability-aware placement, assignment fencing, and graceful drain. */
public interface WorkerFabric {

    void register(ExecutionWorker worker);
    void unregister(WorkerId workerId);
    void heartbeat(WorkerHeartbeat heartbeat);
    Optional<ExecutionWorker> get(WorkerId workerId);
    List<ExecutionWorker> findCapable(WorkerCapabilities requirements);

    /** Finds workers satisfying both runtime capabilities and live resource availability. */
    default List<ExecutionWorker> findCapable(WorkerRequirements requirements) {
        if (requirements == null) return findCapable((WorkerCapabilities) null);
        return findCapable(new WorkerCapabilities(
                requirements.environment() == null ? java.util.Set.of() : java.util.Set.of(requirements.environment()),
                requirements.executor() == null ? java.util.Set.of() : java.util.Set.of(requirements.executor()),
                requirements.requiresGpu(), requirements.cpuCores(), requirements.memoryMb()))
                .stream().filter(requirements::isSatisfiedBy).toList();
    }

    ExecutionAssignment assign(ExecutionId executionId, NodeId nodeId, WorkerId workerId, AssignmentConstraints constraints);

    /** Assign only when the selected worker is live, ready, and has sufficient resources. */
    default ExecutionAssignment assign(ExecutionId executionId, NodeId nodeId, WorkerId workerId,
                                       WorkerRequirements requirements, AssignmentConstraints constraints) {
        ExecutionWorker worker = get(workerId).orElseThrow(
                () -> new IllegalStateException("Worker is not registered: " + workerId.value()));
        if (worker.status() != WorkerStatus.READY || !worker.health().healthy()
                || worker.lease().isExpired() || !requirements.isSatisfiedBy(worker)) {
            throw new IllegalStateException("Worker is not eligible for the requested assignment");
        }
        return assign(executionId, nodeId, workerId, constraints);
    }

    boolean validateFence(AssignmentFence fence);

    /** Stop new work while allowing current assignments to finish. */
    default WorkerDrainResult beginDrain(WorkerId workerId) {
        throw new UnsupportedOperationException("Graceful drain is not supported by this fabric");
    }

    /** Report whether a draining worker has no outstanding assignments. */
    default WorkerDrainResult drainStatus(WorkerId workerId) {
        throw new UnsupportedOperationException("Graceful drain is not supported by this fabric");
    }
}
