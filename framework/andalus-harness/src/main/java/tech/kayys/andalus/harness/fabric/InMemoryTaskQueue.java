package tech.kayys.andalus.harness.fabric;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Process-local queue adapter for tests and development only.
 * It is not durable and does not coordinate claims across JVMs.
 */
public final class InMemoryTaskQueue implements TaskQueue {
    private static final class Entry {
        private final String key;
        private final ExecutionAssignment assignment;
        private Instant availableAt;
        private WorkerId owner;
        private Instant expiresAt;
        private long generation;
        private boolean acknowledged;

        private Entry(String key, ExecutionAssignment assignment, Instant availableAt) {
            this.key = key;
            this.assignment = assignment;
            this.availableAt = availableAt;
        }
    }

    private final Clock clock;
    private final Map<AssignmentId, Entry> entries = new LinkedHashMap<>();
    private final Map<String, AssignmentId> idempotencyKeys = new HashMap<>();

    public InMemoryTaskQueue() { this(Clock.systemUTC()); }

    public InMemoryTaskQueue(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "clock cannot be null");
    }

    @Override
    public synchronized void enqueue(String idempotencyKey, ExecutionAssignment assignment) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("idempotencyKey cannot be blank");
        }
        Objects.requireNonNull(assignment, "assignment cannot be null");
        AssignmentId existingId = idempotencyKeys.get(idempotencyKey);
        if (existingId != null) {
            if (!existingId.equals(assignment.id())) {
                throw new IllegalStateException("Idempotency key already belongs to another assignment");
            }
            return;
        }
        Entry existing = entries.get(assignment.id());
        if (existing != null) {
            throw new IllegalStateException("Assignment is already enqueued with another idempotency key");
        }
        entries.put(assignment.id(), new Entry(idempotencyKey, assignment, clock.instant()));
        idempotencyKeys.put(idempotencyKey, assignment.id());
    }

    @Override
    public synchronized List<TaskClaim> claim(WorkerId workerId, int maxTasks, Duration visibilityTimeout) {
        Objects.requireNonNull(workerId, "workerId cannot be null");
        requirePositive(visibilityTimeout, "visibilityTimeout");
        if (maxTasks < 1) throw new IllegalArgumentException("maxTasks must be positive");
        Instant now = clock.instant();
        List<Entry> eligible = entries.values().stream()
                .filter(e -> !e.acknowledged)
                .filter(e -> !e.availableAt.isAfter(now))
                .filter(e -> e.owner == null || e.expiresAt == null || !e.expiresAt.isAfter(now))
                .sorted(Comparator.comparing(e -> e.availableAt))
                .limit(maxTasks).toList();
        List<TaskClaim> claims = new ArrayList<>(eligible.size());
        for (Entry entry : eligible) {
            entry.generation++;
            entry.owner = workerId;
            entry.expiresAt = now.plus(visibilityTimeout);
            TaskClaimToken token = new TaskClaimToken(entry.assignment.id(), workerId, entry.generation);
            claims.add(new TaskClaim(entry.assignment, token, now, entry.expiresAt));
        }
        return List.copyOf(claims);
    }

    @Override
    public synchronized boolean acknowledge(TaskClaimToken token) {
        Entry entry = current(token);
        if (entry == null) return false;
        entry.acknowledged = true;
        entry.owner = null;
        entry.expiresAt = null;
        return true;
    }

    @Override
    public synchronized boolean negativeAcknowledge(TaskClaimToken token, Duration delay) {
        requireNonNegative(delay, "delay");
        Entry entry = current(token);
        if (entry == null) return false;
        entry.owner = null;
        entry.expiresAt = null;
        entry.availableAt = clock.instant().plus(delay);
        return true;
    }

    @Override
    public synchronized boolean extendVisibility(TaskClaimToken token, Duration extension) {
        requirePositive(extension, "extension");
        Entry entry = current(token);
        if (entry == null) return false;
        entry.expiresAt = entry.expiresAt.plus(extension);
        return true;
    }

    private Entry current(TaskClaimToken token) {
        if (token == null) return null;
        Entry entry = entries.get(token.assignmentId());
        if (entry == null || entry.acknowledged || entry.owner == null) return null;
        Instant now = clock.instant();
        if (!entry.owner.equals(token.workerId()) || entry.generation != token.generation()
                || entry.expiresAt == null || !entry.expiresAt.isAfter(now)) return null;
        return entry;
    }

    private static void requirePositive(Duration duration, String name) {
        Objects.requireNonNull(duration, name + " cannot be null");
        if (duration.isZero() || duration.isNegative()) {
            throw new IllegalArgumentException(name + " must be positive");
        }
    }

    private static void requireNonNegative(Duration duration, String name) {
        Objects.requireNonNull(duration, name + " cannot be null");
        if (duration.isNegative()) throw new IllegalArgumentException(name + " cannot be negative");
    }
}
