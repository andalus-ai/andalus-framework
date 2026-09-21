package tech.kayys.andalus.agent.orchestration.coordination;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe in-memory lease manager with monotonic epoch incrementing.
 */
public class InMemoryCoordinationLeaseManager implements CoordinationLeaseManager {

    private final Map<String, CoordinationLease> leases = new ConcurrentHashMap<>();
    private final AtomicLong epochCounter = new AtomicLong(1);

    @Override
    public synchronized Optional<CoordinationLease> acquire(String resource, String participantId, long ttlSeconds) {
        CoordinationLease existing = leases.get(resource);
        if (existing != null && !existing.isExpired() && !existing.participantId().equals(participantId)) {
            return Optional.empty(); // resource is already locked by another participant
        }

        long epoch = epochCounter.getAndIncrement();
        String leaseId = "lease-" + UUID.randomUUID();
        CoordinationLease lease = CoordinationLease.of(leaseId, resource, participantId, epoch, ttlSeconds);
        leases.put(resource, lease);
        return Optional.of(lease);
    }

    @Override
    public synchronized void release(CoordinationLease lease) {
        if (lease == null) return;
        CoordinationLease current = leases.get(lease.resource());
        if (current != null && current.leaseId().equals(lease.leaseId())) {
            leases.remove(lease.resource());
        }
    }

    @Override
    public Optional<CoordinationLease> activeLease(String resource) {
        CoordinationLease current = leases.get(resource);
        if (current != null && !current.isExpired()) {
            return Optional.of(current);
        }
        return Optional.empty();
    }
}
