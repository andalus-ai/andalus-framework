package tech.kayys.andalus.harness.fabric;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Process-local test adapter; production deployments must use a shared durable store. */
public final class InMemoryCompletionIntentStore implements CompletionIntentStore {
    private final Map<String, CompletionIntent> intents = new ConcurrentHashMap<>();

    @Override
    public CompletionIntent createIfAbsent(CompletionIntent intent) {
        Objects.requireNonNull(intent, "intent cannot be null");
        CompletionIntent existing = intents.putIfAbsent(intent.idempotencyKey(), intent);
        if (existing == null) return intent;
        if (!existing.hasSameOperation(intent))
            throw new IllegalStateException("Completion idempotency key is already bound to another operation");
        return existing;
    }

    @Override
    public Optional<CompletionIntent> get(String idempotencyKey) {
        if (idempotencyKey == null) return Optional.empty();
        return Optional.ofNullable(intents.get(idempotencyKey));
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
