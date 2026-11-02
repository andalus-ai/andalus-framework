package tech.kayys.andalus.harness.fabric;

import tech.kayys.andalus.harness.consistency.state.ExecutionId;
import tech.kayys.andalus.workflow.graph.NodeId;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-process worker fabric. Its maps are suitable for tests and single-process development;
 * distributed deployments must provide shared registration and assignment coordination.
 */
public class DefaultWorkerFabric implements WorkerFabric {

    private final Map<WorkerId, ExecutionWorker> workers = new ConcurrentHashMap<>();
    private final Map<AssignmentId, Long> activeGenerations = new ConcurrentHashMap<>();
    private final Map<AssignmentId, WorkerId> assignmentOwners = new ConcurrentHashMap<>();
    private final Map<WorkerId, Set<AssignmentId>> workerAssignments = new ConcurrentHashMap<>();
    private final AtomicLong generationSequence = new AtomicLong(1L);
    private final Duration heartbeatTimeout;

    public DefaultWorkerFabric() {
        this(Duration.ofMinutes(2));
    }

    public DefaultWorkerFabric(Duration heartbeatTimeout) {
        if (heartbeatTimeout == null || heartbeatTimeout.isNegative() || heartbeatTimeout.isZero()) {
            throw new IllegalArgumentException("heartbeatTimeout must be positive");
        }
        this.heartbeatTimeout = heartbeatTimeout;
    }

    @Override
    public void register(ExecutionWorker worker) {
        Objects.requireNonNull(worker, "ExecutionWorker cannot be null");
        if (worker.lease().isExpired()) {
            throw new IllegalArgumentException("Cannot register a worker with an expired lease");
        }
        workers.put(worker.id(), worker);
        workerAssignments.computeIfAbsent(worker.id(), ignored -> ConcurrentHashMap.newKeySet());
    }

    @Override
    public void unregister(WorkerId workerId) {
        Objects.requireNonNull(workerId, "WorkerId cannot be null");
        if (!workerAssignments.getOrDefault(workerId, Set.of()).isEmpty()) {
            throw new IllegalStateException("Cannot unregister a worker with active assignments; drain it first");
        }
        workers.remove(workerId);
        workerAssignments.remove(workerId);
    }

    @Override
    public void heartbeat(WorkerHeartbeat heartbeat) {
        Objects.requireNonNull(heartbeat, "WorkerHeartbeat cannot be null");
        workers.computeIfPresent(heartbeat.workerId(), (id, existing) -> {
            Instant previous = existing.health().lastHeartbeat();
            if (previous != null && heartbeat.timestamp().isBefore(previous)) {
                return existing;
            }
            if (existing.status() == WorkerStatus.DRAINING && heartbeat.status().isAvailable()) {
                return existing;
            }
            if (existing instanceof DefaultExecutionWorker defaultWorker) {
                WorkerHealth health = heartbeat.health();
                return defaultWorker
                        .withStatus(heartbeat.status())
                        .withResources(heartbeat.resources())
                        .withHealth(health)
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
        Instant now = Instant.now();
        return workers.values().stream()
                .filter(w -> w.status().isAvailable() && w.health().healthy())
                .filter(w -> w.health().lastHeartbeat() == null
                        || !now.isAfter(w.health().lastHeartbeat().plus(heartbeatTimeout)))
                .filter(w -> !w.lease().isExpired())
                .filter(w -> {
                    if (requirements == null) return true;
                    if (requirements.hasGpu() && !w.capabilities().hasGpu()) return false;
                    if (w.capabilities().maxCpuCores() < requirements.maxCpuCores()) return false;
                    return w.capabilities().maxMemoryMb() >= requirements.maxMemoryMb();
                })
                .toList();
    }

    @Override
    public List<ExecutionWorker> findCapable(WorkerRequirements requirements) {
        Objects.requireNonNull(requirements, "requirements cannot be null");
        return findCapable(new WorkerCapabilities(
                requirements.environment() == null ? Set.of() : Set.of(requirements.environment()),
                requirements.executor() == null ? Set.of() : Set.of(requirements.executor()),
                requirements.requiresGpu(), requirements.cpuCores(), requirements.memoryMb()))
                .stream().filter(requirements::isSatisfiedBy).toList();
    }

    @Override
    public synchronized ExecutionAssignment assign(
            ExecutionId executionId,
            NodeId nodeId,
            WorkerId workerId,
            AssignmentConstraints constraints
    ) {
        Objects.requireNonNull(executionId, "ExecutionId cannot be null");
        Objects.requireNonNull(nodeId, "NodeId cannot be null");
        Objects.requireNonNull(workerId, "WorkerId cannot be null");
        ExecutionWorker worker = workers.get(workerId);
        if (worker == null) throw new IllegalStateException("Worker is not registered");
        if (worker.status() != WorkerStatus.READY || !worker.health().healthy()
                || worker.lease().isExpired()) {
            throw new IllegalStateException("Worker is not ready to accept assignments");
        }
        long generation = generationSequence.getAndIncrement();
        DefaultExecutionAssignment assignment = DefaultExecutionAssignment.create(
                executionId, nodeId, workerId, generation,
                constraints != null ? constraints : AssignmentConstraints.defaults());
        activeGenerations.put(assignment.id(), generation);
        assignmentOwners.put(assignment.id(), workerId);
        workerAssignments.computeIfAbsent(workerId, ignored -> ConcurrentHashMap.newKeySet())
                .add(assignment.id());
        return assignment;
    }

    @Override
    public synchronized ExecutionAssignment assign(
            ExecutionId executionId, NodeId nodeId, WorkerId workerId,
            WorkerRequirements requirements, AssignmentConstraints constraints) {
        ExecutionWorker worker = workers.get(workerId);
        if (worker == null || requirements == null || !requirements.isSatisfiedBy(worker)) {
            throw new IllegalStateException("Worker does not satisfy task requirements");
        }
        return assign(executionId, nodeId, workerId, constraints);
    }

    @Override
    public boolean validateFence(AssignmentFence fence) {
        if (fence == null) return false;
        Long expected = activeGenerations.get(fence.assignmentId());
        WorkerId owner = assignmentOwners.get(fence.assignmentId());
        ExecutionWorker worker = owner == null ? null : workers.get(owner);
        return expected != null && worker != null && !worker.lease().isExpired()
                && fence.isValid(expected);
    }

    /** Complete an assignment and release its worker slot. Stale fences are rejected. */
    public synchronized boolean completeAssignment(AssignmentFence fence) {
        if (!validateFence(fence)) return false;
        WorkerId owner = assignmentOwners.remove(fence.assignmentId());
        activeGenerations.remove(fence.assignmentId());
        Set<AssignmentId> assignments = workerAssignments.get(owner);
        if (assignments != null) assignments.remove(fence.assignmentId());
        return true;
    }

    @Override
    public WorkerDrainResult beginDrain(WorkerId workerId) {
        Objects.requireNonNull(workerId, "WorkerId cannot be null");
        ExecutionWorker worker = workers.computeIfPresent(workerId, (id, current) -> {
            if (current instanceof DefaultExecutionWorker defaultWorker) {
                return defaultWorker.withStatus(WorkerStatus.DRAINING);
            }
            return current;
        });
        if (worker == null) throw new IllegalArgumentException("Worker is not registered");
        return drainStatus(workerId);
    }

    @Override
    public WorkerDrainResult drainStatus(WorkerId workerId) {
        ExecutionWorker worker = workers.get(Objects.requireNonNull(workerId, "WorkerId cannot be null"));
        if (worker == null) throw new IllegalArgumentException("Worker is not registered");
        int active = workerAssignments.getOrDefault(workerId, Set.of()).size();
        boolean draining = worker.status() == WorkerStatus.DRAINING;
        return new WorkerDrainResult(workerId, draining, draining && active == 0, active);
    }
}
