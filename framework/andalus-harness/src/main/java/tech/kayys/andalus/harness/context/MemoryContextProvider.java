package tech.kayys.andalus.harness.context;

import tech.kayys.andalus.harness.memory.MemoryContext;
import tech.kayys.andalus.harness.memory.MemoryQuery;
import tech.kayys.andalus.harness.memory.MemoryQueryResult;
import tech.kayys.andalus.harness.memory.MemoryRuntime;
import tech.kayys.andalus.harness.memory.MemoryScope;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * {@link ContextProvider} that retrieves relevant memory entries from {@link MemoryRuntime}
 * and adapts them into {@link ContextContribution} items of kind {@link ContextKind#MEMORY}.
 *
 * <p>This is the canonical runtime bridge between the memory subsystem (Phase 3F–3I)
 * and the context assembly pipeline (Phase 3K). It is registered with
 * {@link DefaultContextAssembler} so that every agent turn automatically surfaces
 * contextually-relevant memories before inference.</p>
 *
 * <p>Thread-safe: all state is either immutable or delegated to thread-safe collaborators.</p>
 */
public class MemoryContextProvider implements ContextProvider {

    static final String SOURCE_ID = "memory-runtime";

    private final MemoryRuntime memoryRuntime;
    private final MemoryContext memoryContext;
    private final MemoryScope scope;
    private final int topK;
    private final ContextPriority priority;

    public MemoryContextProvider(
            MemoryRuntime memoryRuntime,
            MemoryContext memoryContext) {
        this(memoryRuntime, memoryContext, null, 10, ContextPriority.HIGH);
    }

    public MemoryContextProvider(
            MemoryRuntime memoryRuntime,
            MemoryContext memoryContext,
            MemoryScope scope,
            int topK,
            ContextPriority priority) {
        this.memoryRuntime = Objects.requireNonNull(memoryRuntime, "memoryRuntime");
        this.memoryContext = Objects.requireNonNull(memoryContext, "memoryContext");
        this.scope = scope;
        this.topK = topK < 1 ? 10 : topK;
        this.priority = priority == null ? ContextPriority.HIGH : priority;
    }

    @Override
    public ContextContribution provide(ContextRequest request) {
        Objects.requireNonNull(request, "request");

        // Skip if caller did not request MEMORY kind (empty kinds means "all")
        if (!request.kinds().isEmpty() && !request.kinds().contains(ContextKind.MEMORY)) {
            return ContextContribution.of(SOURCE_ID, List.of());
        }

        String queryText = request.query();
        if (queryText == null || queryText.isBlank()) {
            return ContextContribution.of(SOURCE_ID, List.of());
        }

        MemoryQuery query = new MemoryQuery(queryText, scope, topK, Map.of());

        MemoryQueryResult result;
        try {
            result = memoryRuntime.retrieve(query, memoryContext)
                    .toCompletableFuture()
                    .join();
        } catch (Exception e) {
            // Degrade gracefully — a memory retrieval failure must not abort context assembly
            return ContextContribution.of(SOURCE_ID, List.of());
        }

        if (result == null || result.entries().isEmpty()) {
            return ContextContribution.of(SOURCE_ID, List.of());
        }

        List<ContextItem> items = result.entries().stream()
                .map(entry -> new ContextItem(
                        "mem-" + entry.id().value(),
                        ContextKind.MEMORY,
                        entry.content(),
                        ContextSource.of(SOURCE_ID, "memory-runtime"),
                        priority,
                        java.util.Map.of(
                                "memoryType", entry.type().name(),
                                "memoryScope", entry.scope().name()
                        )
                ))
                .toList();

        return new ContextContribution(SOURCE_ID, priority, items);
    }
}
