package tech.kayys.andalus.harness.memory;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

/**
 * Adapts a synchronous MemoryStore to the AsyncMemoryStore contract.
 */
public final class DefaultAsyncMemoryStore implements AsyncMemoryStore {

    private final MemoryStore delegate;
    private final Executor executor;

    public DefaultAsyncMemoryStore(MemoryStore delegate, Executor executor) {
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null");
        this.executor = executor != null ? executor : ForkJoinPool.commonPool();
    }

    public DefaultAsyncMemoryStore(MemoryStore delegate) {
        this(delegate, ForkJoinPool.commonPool());
    }

    @Override
    public CompletionStage<MemoryId> store(MemoryEntry entry) {
        return CompletableFuture.supplyAsync(() -> delegate.store(entry), executor);
    }

    @Override
    public CompletionStage<Optional<MemoryEntry>> get(MemoryId id) {
        return CompletableFuture.supplyAsync(() -> delegate.get(id), executor);
    }

    @Override
    public CompletionStage<Void> delete(MemoryId id) {
        return CompletableFuture.runAsync(() -> delegate.delete(id), executor);
    }

    @Override
    public CompletionStage<MemoryQueryResult> query(MemoryQuery query) {
        return CompletableFuture.supplyAsync(() -> delegate.query(query), executor);
    }
}
