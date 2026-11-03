package tech.kayys.andalus.harness.fabric;

import java.util.Optional;

/**
 * Durable idempotency/outbox port. Implementations must atomically create by key
 * and compare-and-set stage transitions across all workers and reconcilers.
 */
public interface CompletionIntentStore {
    CompletionIntent createIfAbsent(CompletionIntent intent);

    Optional<CompletionIntent> get(String idempotencyKey);

    boolean advance(String idempotencyKey, CompletionIntentStage expected,
                    CompletionIntentStage next);
}
