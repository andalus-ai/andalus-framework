package tech.kayys.wayang.execution.core.mobility;

import tech.kayys.wayang.execution.mobility.ExecutionLease;
import tech.kayys.wayang.execution.mobility.FencingToken;
import tech.kayys.wayang.execution.mobility.LeaseManager;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe default implementation of {@link LeaseManager} with generation-based fencing tokens.
 */
public class DefaultLeaseManager implements LeaseManager {

    private final Map<String, ExecutionLease> leases = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> generations = new ConcurrentHashMap<>();

    @Override
    public ExecutionLease acquireLease(String executionId, String holderId, Duration duration) {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(holderId, "holderId cannot be null");
        Objects.requireNonNull(duration, "duration cannot be null");

        AtomicLong genCounter = generations.computeIfAbsent(executionId, k -> new AtomicLong(0L));
        long newGen = genCounter.incrementAndGet();
        FencingToken token = FencingToken.of(newGen);

        Instant now = Instant.now();
        Instant expiresAt = now.plus(duration);

        ExecutionLease lease = new ExecutionLease(executionId, holderId, token, now, expiresAt);
        leases.put(executionId, lease);
        return lease;
    }

    @Override
    public boolean renewLease(String executionId, String holderId, Duration duration) {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(holderId, "holderId cannot be null");

        ExecutionLease current = leases.get(executionId);
        if (current == null || !current.holderId().equals(holderId) || current.isExpired()) {
            return false;
        }

        Instant now = Instant.now();
        ExecutionLease renewed = new ExecutionLease(
                executionId,
                holderId,
                current.fencingToken(),
                current.acquiredAt(),
                now.plus(duration)
        );
        leases.put(executionId, renewed);
        return true;
    }

    @Override
    public void releaseLease(String executionId, String holderId) {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        ExecutionLease current = leases.get(executionId);
        if (current != null && current.holderId().equals(holderId)) {
            leases.remove(executionId);
        }
    }

    @Override
    public boolean validateToken(String executionId, FencingToken token) {
        if (executionId == null || token == null) {
            return false;
        }
        AtomicLong genCounter = generations.get(executionId);
        if (genCounter == null) {
            return false;
        }
        return genCounter.get() == token.generation();
    }

    @Override
    public Optional<FencingToken> currentToken(String executionId) {
        if (executionId == null) {
            return Optional.empty();
        }
        AtomicLong genCounter = generations.get(executionId);
        if (genCounter == null) {
            return Optional.empty();
        }
        return Optional.of(FencingToken.of(genCounter.get()));
    }

    @Override
    public Optional<ExecutionLease> getLease(String executionId) {
        if (executionId == null) {
            return Optional.empty();
        }
        ExecutionLease lease = leases.get(executionId);
        if (lease != null && !lease.isExpired()) {
            return Optional.of(lease);
        }
        return Optional.empty();
    }
}
