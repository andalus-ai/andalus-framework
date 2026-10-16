package tech.kayys.andalus.harness.memory;

import java.util.concurrent.CompletionStage;

/**
 * Asynchronous, context-aware memory retriever contract.
 */
public interface ContextualMemoryRetriever {

    CompletionStage<MemoryQueryResult> retrieve(
            MemoryQuery query,
            MemoryContext context
    );
}
