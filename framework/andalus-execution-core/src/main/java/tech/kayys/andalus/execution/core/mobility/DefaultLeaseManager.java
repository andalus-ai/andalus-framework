package tech.kayys.andalus.execution.core.mobility;

import tech.kayys.andalus.execution.mobility.ExecutionLease;
import tech.kayys.andalus.execution.mobility.FencingToken;
import tech.kayys.andalus.execution.mobility.LeaseManager;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Thread-safe in-memory lease manager with generation-based fencing tokens.
 *
 * <p>This implementation is suitable for a single process only. Distributed deployments
 * must use an adapter backed by an atomic shared coordinator.</p>
 */
public class DefaultLeaseManager implements LeaseManager {

    private final Map<String, ExecutionLease> leases = new HashMap<>();
    private final Map<String, Long> generations = new HashMap<>();

    @Override
    public synchronized ExecutionLease acquireLease(String executionId, String holderId, Duration duration) {
        requireText(executionId, "executionId");
        requireText(holderId, "holderId");
        requireDuration(duration);
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(holderId, "holderId cannot be null");
        Objects.requireNonNull(duration, "duration cannot be null");

        ExecutionLease existing = leases.get(executionId);
        if (existing != null && !existing.isExpired() && !existing.holderId().equals(holderId)) {
            throw new IllegalStateException("Execution lease is still held by " + existing.holderId());
        }

        long generation = generations.getOrDefault(executionId, 0L) + 1L;
        generations.put(executionId, generation);
        Instant now = Instant.now();
        ExecutionLease lease = new ExecutionLease(executionId, holderId, FencingToken.of(generation),
                now, now.plus(duration));
        leases.put(executionId, lease);
        return lease;
    }

    @Override
    public synchronized boolean renewLease(String executionId, String holderId, Duration duration) {
        requireText(executionId, "executionId");
        requireText(holderId, "holderId");
        requireDuration(duration);

        ExecutionLease current = leases.get(executionId);
        if (current == null || current.isExpired() || !current.holderId().equals(holderId)) {
            return false;
        }

        Instant now = Instant.now();
        leases.put(executionId, new ExecutionLease(executionId, holderId, current.fencingToken(),
                current.acquiredAt(), now.plus(duration)));
        return true;
    }

    @Override
    public synchronized void releaseLease(String executionId, String holderId) {
        requireText(executionId, "executionId");
        requireText(holderId, "holderId");
        ExecutionLease current = leases.get(executionId);
        if (current != null && current.holderId().equals(holderId)) {
            leases.remove(executionId);
        }
    }

    @Override
    public synchronized boolean validateToken(String executionId, FencingToken token) {
        if (executionId == null || token == null) {
            return false;
        }
        ExecutionLease active = leases.get(executionId);
        return active != null && active.isValidFor(active.holderId(), token)
                && generations.getOrDefault(executionId, 0L) == token.generation();
    }

    @Override
    public synchronized Optional<FencingToken> currentToken(String executionId) {
        if (executionId == null) {
            return Optional.empty();
        }
        Long generation = generations.get(executionId);
        return generation == null ? Optional.empty() : Optional.of(FencingToken.of(generation));
    }

    @Override
    public synchronized Optional<ExecutionLease> getLease(String executionId) {
        if (executionId == null) {
            return Optional.empty();
        }
        ExecutionLease lease = leases.get(executionId);
        if (lease == null || lease.isExpired()) {
            if (lease != null) {
                leases.remove(executionId);
            }
            return Optional.empty();
        }
        return Optional.of(lease);
    }

    private static void requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }

    private static void requireDuration(Duration duration) {
        Objects.requireNonNull(duration, "duration cannot be null");
        if (duration.isZero() || duration.isNegative()) {
            throw new IllegalArgumentException("duration must be positive");
        }
    }
}
