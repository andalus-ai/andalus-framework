package tech.kayys.andalus.harness.memory;

import java.util.concurrent.CompletionStage;

/**
 * Canonical runtime orchestration service for agent memory operations.
 */
public interface MemoryRuntime {

    CompletionStage<MemoryQueryResult> retrieve(
            MemoryQuery query,
            MemoryContext context
    );

    CompletionStage<MemoryWriteResult> remember(
            MemoryEntry candidate,
            MemoryContext context
    );

    CompletionStage<Void> delete(
            MemoryId memoryId,
            MemoryContext context
    );
}
