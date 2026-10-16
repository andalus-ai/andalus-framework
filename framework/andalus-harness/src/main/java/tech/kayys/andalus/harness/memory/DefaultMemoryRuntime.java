package tech.kayys.andalus.harness.memory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Default canonical implementation of MemoryRuntime.
 */
public final class DefaultMemoryRuntime implements MemoryRuntime {

    private final AsyncMemoryStore store;
    private final ContextualMemoryRetriever retriever;
    private final MemoryAdmissionPolicy admissionPolicy;
    private final MemoryConflictResolver conflictResolver;

    public DefaultMemoryRuntime(
            AsyncMemoryStore store,
            ContextualMemoryRetriever retriever,
            MemoryAdmissionPolicy admissionPolicy,
            MemoryConflictResolver conflictResolver) {
        this.store = Objects.requireNonNull(store, "store must not be null");
        this.retriever = retriever != null ? retriever : (q, ctx) -> store.query(q);
        this.admissionPolicy = admissionPolicy != null ? admissionPolicy : MemoryAdmissionPolicy.allowAll();
        this.conflictResolver = conflictResolver != null ? conflictResolver : MemoryConflictResolver.highestConfidence();
    }

    public DefaultMemoryRuntime(MemoryStore syncStore) {
        this(new DefaultAsyncMemoryStore(syncStore), null, null, null);
    }

    @Override
    public CompletionStage<MemoryQueryResult> retrieve(MemoryQuery query, MemoryContext context) {
        Objects.requireNonNull(query, "query must not be null");
        MemoryContext effectiveContext = context != null ? context : MemoryContext.empty();
        return retriever.retrieve(query, effectiveContext);
    }

    @Override
    public CompletionStage<MemoryWriteResult> remember(MemoryEntry candidate, MemoryContext context) {
        Objects.requireNonNull(candidate, "candidate must not be null");

        // 1. Admission policy check
        if (!admissionPolicy.shouldAdmit(candidate)) {
            return CompletableFuture.completedFuture(MemoryWriteResult.rejected("Rejected by admission policy"));
        }

        // 2. Query potential conflicts/duplicates
        MemoryQuery conflictQuery = new MemoryQuery(candidate.content(), candidate.scope(), 5, java.util.Map.of());
        return store.query(conflictQuery)
                .thenCompose(existing -> {
                    if (existing != null && !existing.entries().isEmpty()) {
                        // Conflict resolution
                        List<MemoryEntry> candidates = new java.util.ArrayList<>(existing.entries());
                        candidates.add(candidate);
                        MemoryEntry resolved = conflictResolver.resolve(candidates);
                        if (resolved != candidate && existing.entries().contains(resolved)) {
                            return CompletableFuture.completedFuture(
                                    MemoryWriteResult.conflict("Existing memory preferred over candidate"));
                        }
                    }
                    return store.store(candidate)
                            .thenApply(MemoryWriteResult::admitted);
                });
    }

    @Override
    public CompletionStage<Void> delete(MemoryId memoryId, MemoryContext context) {
        Objects.requireNonNull(memoryId, "memoryId must not be null");
        return store.delete(memoryId);
    }
}
