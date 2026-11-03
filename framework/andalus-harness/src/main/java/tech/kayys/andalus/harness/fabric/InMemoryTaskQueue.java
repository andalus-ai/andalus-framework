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

/** Process-local reference queue; not durable or cross-JVM. */
public final class InMemoryTaskQueue implements TaskQueue {
    private static final class Entry {
        private final String key;
        private final ExecutionAssignment assignment;
        private Instant availableAt;
        private WorkerId owner;
        private Instant expiresAt;
        private long generation;
        private int failureCount;
        private String failureCategory;
        private String failureMessage;
        private Instant deadLetteredAt;
        private boolean acknowledged;
        private WorkerId acknowledgedBy;
        private long acknowledgedGeneration;

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
        if (idempotencyKey == null || idempotencyKey.isBlank()) throw new IllegalArgumentException("idempotencyKey cannot be blank");
        Objects.requireNonNull(assignment, "assignment cannot be null");
        AssignmentId existingId = idempotencyKeys.get(idempotencyKey);
        if (existingId != null) {
            if (!existingId.equals(assignment.id())) throw new IllegalStateException("Idempotency key already belongs to another assignment");
            return;
        }
        if (entries.containsKey(assignment.id())) throw new IllegalStateException("Assignment is already enqueued with another idempotency key");
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
                .filter(e -> !e.acknowledged && e.deadLetteredAt == null)
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
        if (token == null) return false;
        Entry entry = entries.get(token.assignmentId());
        if (entry == null) return false;
        if (entry.acknowledged) {
            return token.workerId().equals(entry.acknowledgedBy)
                    && token.generation() == entry.acknowledgedGeneration;
        }
        if (current(token) == null) return false;
        entry.acknowledged = true;
        entry.acknowledgedBy = token.workerId();
        entry.acknowledgedGeneration = token.generation();
        clearClaim(entry);
        return true;
    }

    @Override
    public synchronized boolean negativeAcknowledge(TaskClaimToken token, Duration delay) {
        requireNonNegative(delay, "delay");
        Entry entry = current(token);
        if (entry == null) return false;
        entry.availableAt = clock.instant().plus(delay);
        clearClaim(entry);
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

    @Override
    public synchronized TaskFailureResult fail(TaskClaimToken token, TaskFailureDecision decision) {
        Objects.requireNonNull(decision, "decision cannot be null");
        Entry entry = current(token);
        if (entry == null) return TaskFailureResult.LOST_CLAIM;
        entry.failureCount++;
        entry.failureCategory = decision.category();
        entry.failureMessage = decision.message();
        boolean retry = decision.retryDelay().isPresent()
                && entry.failureCount <= entry.assignment.constraints().maxRetries();
        if (retry) {
            entry.availableAt = clock.instant().plus(decision.retryDelay().orElseThrow());
            clearClaim(entry);
            return TaskFailureResult.RETRY_SCHEDULED;
        }
        entry.deadLetteredAt = clock.instant();
        clearClaim(entry);
        return TaskFailureResult.DEAD_LETTERED;
    }

    @Override
    public synchronized List<DeadLetterTask> listDeadLetters(int limit) {
        if (limit < 1) throw new IllegalArgumentException("limit must be positive");
        return entries.values().stream()
                .filter(e -> e.deadLetteredAt != null)
                .sorted(Comparator.comparing(e -> e.deadLetteredAt))
                .limit(limit)
                .map(e -> new DeadLetterTask(e.assignment, e.failureCount,
                        e.failureCategory, e.failureMessage, e.deadLetteredAt))
                .toList();
    }

    private Entry current(TaskClaimToken token) {
        if (token == null) return null;
        Entry entry = entries.get(token.assignmentId());
        if (entry == null || entry.acknowledged || entry.deadLetteredAt != null || entry.owner == null) return null;
        Instant now = clock.instant();
        if (!entry.owner.equals(token.workerId()) || entry.generation != token.generation()
                || entry.expiresAt == null || !entry.expiresAt.isAfter(now)) return null;
        return entry;
    }

    private static void clearClaim(Entry entry) {
        entry.owner = null;
        entry.expiresAt = null;
    }
    private static void requirePositive(Duration duration, String name) {
        Objects.requireNonNull(duration, name + " cannot be null");
        if (duration.isZero() || duration.isNegative()) throw new IllegalArgumentException(name + " must be positive");
    }
    private static void requireNonNegative(Duration duration, String name) {
        Objects.requireNonNull(duration, name + " cannot be null");
        if (duration.isNegative()) throw new IllegalArgumentException(name + " cannot be negative");
    }
}
