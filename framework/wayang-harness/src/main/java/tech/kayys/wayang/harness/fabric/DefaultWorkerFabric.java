package tech.kayys.wayang.harness.fabric;

import tech.kayys.wayang.harness.consistency.state.ExecutionId;
import tech.kayys.wayang.harness.workflow.NodeId;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Standard implementation of {@link WorkerFabric} with assignment fencing.
 */
public class DefaultWorkerFabric implements WorkerFabric {

    private final Map<WorkerId, ExecutionWorker> workers = new ConcurrentHashMap<>();
    private final Map<AssignmentId, Long> activeGenerations = new ConcurrentHashMap<>();
    private final AtomicLong generationSequence = new AtomicLong(1L);

    @Override
    public void register(ExecutionWorker worker) {
        Objects.requireNonNull(worker, "ExecutionWorker cannot be null");
        workers.put(worker.id(), worker);
    }

    @Override
    public void unregister(WorkerId workerId) {
        Objects.requireNonNull(workerId, "WorkerId cannot be null");
        workers.remove(workerId);
    }

    @Override
    public void heartbeat(WorkerHeartbeat heartbeat) {
        Objects.requireNonNull(heartbeat, "WorkerHeartbeat cannot be null");
        workers.computeIfPresent(heartbeat.workerId(), (id, existing) -> {
            if (existing instanceof DefaultExecutionWorker defaultWorker) {
                return defaultWorker
                        .withStatus(heartbeat.status())
                        .withHealth(heartbeat.health())
                        .withLease(defaultWorker.lease().renew(Duration.ofMinutes(5)));
            }
            return existing;
        });
    }

    @Override
    public Optional<ExecutionWorker> get(WorkerId workerId) {
        Objects.requireNonNull(workerId, "WorkerId cannot be null");
        return Optional.ofNullable(workers.get(workerId));
    }

    @Override
    public List<ExecutionWorker> findCapable(WorkerCapabilities requirements) {
        return workers.values().stream()
                .filter(w -> w.status().isAvailable() && w.health().healthy())
                .filter(w -> {
                    if (requirements == null) return true;
                    if (requirements.hasGpu() && !w.capabilities().hasGpu()) return false;
                    if (w.capabilities().maxCpuCores() < requirements.maxCpuCores()) return false;
                    return true;
                })
                .toList();
    }

    @Override
    public ExecutionAssignment assign(
            ExecutionId executionId,
            NodeId nodeId,
            WorkerId workerId,
            AssignmentConstraints constraints
    ) {
        Objects.requireNonNull(executionId, "ExecutionId cannot be null");
        Objects.requireNonNull(nodeId, "NodeId cannot be null");
        Objects.requireNonNull(workerId, "WorkerId cannot be null");

        long generation = generationSequence.getAndIncrement();
        DefaultExecutionAssignment assignment = DefaultExecutionAssignment.create(
                executionId,
                nodeId,
                workerId,
                generation,
                constraints != null ? constraints : AssignmentConstraints.defaults()
        );

        activeGenerations.put(assignment.id(), generation);
        return assignment;
    }

    @Override
    public boolean validateFence(AssignmentFence fence) {
        if (fence == null) return false;
        Long expected = activeGenerations.get(fence.assignmentId());
        return expected != null && fence.isValid(expected);
    }
}
