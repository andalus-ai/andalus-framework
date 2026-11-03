package tech.kayys.andalus.harness.fabric;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Process-local test adapter; production deployments must use a shared durable store. */
public final class InMemoryCompletionIntentStore implements CompletionIntentStore {
    private final Map<String, CompletionIntent> intents = new ConcurrentHashMap<>();
    private final Map<AssignmentId, String> assignmentIndex = new ConcurrentHashMap<>();

    @Override
    public synchronized CompletionIntent createIfAbsent(CompletionIntent intent) {
        Objects.requireNonNull(intent, "intent cannot be null");
        CompletionIntent existing = intents.get(intent.idempotencyKey());
        if (existing != null) {
            if (!existing.hasSameOperation(intent))
                throw new IllegalStateException("Completion idempotency key is already bound to another operation");
            return existing;
        }
        String existingKey = assignmentIndex.get(intent.assignmentId());
        if (existingKey != null && !existingKey.equals(intent.idempotencyKey()))
            throw new IllegalStateException("Assignment already has a completion intent: " + intent.assignmentId().value());
        intents.put(intent.idempotencyKey(), intent);
        assignmentIndex.put(intent.assignmentId(), intent.idempotencyKey());
        return intent;
    }

    @Override
    public Optional<CompletionIntent> get(String idempotencyKey) {
        if (idempotencyKey == null) return Optional.empty();
        return Optional.ofNullable(intents.get(idempotencyKey));
    }

    @Override
    public Optional<CompletionIntent> findByAssignmentId(AssignmentId assignmentId) {
        Objects.requireNonNull(assignmentId, "assignmentId cannot be null");
        String key = assignmentIndex.get(assignmentId);
        return key == null ? Optional.empty() : get(key);
    }

    @Override
    public List<CompletionIntent> listIncomplete(int limit) {
        if (limit <= 0) throw new IllegalArgumentException("limit must be positive");
        return intents.values().stream()
                .filter(intent -> intent.stage() != CompletionIntentStage.ACKNOWLEDGED)
                .sorted(Comparator.comparing(CompletionIntent::createdAt)
                        .thenComparing(CompletionIntent::idempotencyKey))
                .limit(limit)
                .toList();
    }

    @Override
    public boolean advance(String idempotencyKey, CompletionIntentStage expected,
                           CompletionIntentStage next) {
        Objects.requireNonNull(expected, "expected cannot be null");
        Objects.requireNonNull(next, "next cannot be null");
        if (next.ordinal() != expected.ordinal() + 1)
            throw new IllegalArgumentException("Stage must advance exactly once");
        final boolean[] advanced = {false};
        intents.computeIfPresent(idempotencyKey, (key, current) -> {
            if (current.stage() != expected) return current;
            advanced[0] = true;
            return current.advance(next);
        });
        return advanced[0];
    }
}
