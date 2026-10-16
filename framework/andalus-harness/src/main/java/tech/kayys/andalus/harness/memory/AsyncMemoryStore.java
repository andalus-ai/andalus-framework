package tech.kayys.andalus.harness.memory;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 * Asynchronous contract for long-lived memory storage and query operations.
 */
public interface AsyncMemoryStore {

    CompletionStage<MemoryId> store(MemoryEntry entry);

    CompletionStage<Optional<MemoryEntry>> get(MemoryId id);

    CompletionStage<Void> delete(MemoryId id);

    CompletionStage<MemoryQueryResult> query(MemoryQuery query);
}
