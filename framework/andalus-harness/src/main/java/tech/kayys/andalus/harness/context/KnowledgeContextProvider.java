package tech.kayys.andalus.harness.context;

import tech.kayys.andalus.knowledge.KnowledgeContext;
import tech.kayys.andalus.knowledge.KnowledgeEvidence;
import tech.kayys.andalus.knowledge.KnowledgeQuery;
import tech.kayys.andalus.knowledge.KnowledgeResult;
import tech.kayys.andalus.knowledge.KnowledgeResolver;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * {@link ContextProvider} that retrieves relevant knowledge evidence from {@link KnowledgeResolver}
 * and adapts each piece of evidence into a {@link ContextItem} of kind {@link ContextKind#KNOWLEDGE}.
 *
 * <p>This is the canonical runtime bridge between the knowledge/RAG subsystem (Phase 3D–3L)
 * and the context assembly pipeline (Phase 3K). It is registered with
 * {@link DefaultContextAssembler} so that every agent turn retrieves domain knowledge before
 * model inference.</p>
 *
 * <p>Thread-safe: all state is either immutable or delegated to thread-safe collaborators.</p>
 */
public class KnowledgeContextProvider implements ContextProvider {

    static final String SOURCE_ID = "knowledge-resolver";

    private final KnowledgeResolver resolver;
    private final KnowledgeContext knowledgeContext;
    private final int topK;
    private final double minScore;
    private final ContextPriority priority;

    public KnowledgeContextProvider(
            KnowledgeResolver resolver,
            KnowledgeContext knowledgeContext) {
        this(resolver, knowledgeContext, 5, 0.0, ContextPriority.HIGH);
    }

    public KnowledgeContextProvider(
            KnowledgeResolver resolver,
            KnowledgeContext knowledgeContext,
            int topK,
            double minScore,
            ContextPriority priority) {
        this.resolver = Objects.requireNonNull(resolver, "resolver");
        this.knowledgeContext = Objects.requireNonNull(knowledgeContext, "knowledgeContext");
        this.topK = topK < 1 ? 5 : topK;
        this.minScore = Math.max(0.0, minScore);
        this.priority = priority == null ? ContextPriority.HIGH : priority;
    }

    @Override
    public ContextContribution provide(ContextRequest request) {
        Objects.requireNonNull(request, "request");

        // Skip if caller did not request KNOWLEDGE kind (empty kinds means "all")
        if (!request.kinds().isEmpty() && !request.kinds().contains(ContextKind.KNOWLEDGE)) {
            return ContextContribution.of(SOURCE_ID, List.of());
        }

        String queryText = request.query();
        if (queryText == null || queryText.isBlank()) {
            return ContextContribution.of(SOURCE_ID, List.of());
        }

        KnowledgeQuery query = KnowledgeQuery.of(queryText)
                .withTopK(topK)
                .withMinScore(minScore);

        KnowledgeResult result;
        try {
            result = resolver.resolve(query, knowledgeContext)
                    .toCompletableFuture()
                    .join();
        } catch (Exception e) {
            // Degrade gracefully — a knowledge retrieval failure must not abort context assembly
            return ContextContribution.of(SOURCE_ID, List.of());
        }

        if (result == null || result.evidence().isEmpty()) {
            return ContextContribution.of(SOURCE_ID, List.of());
        }

        List<ContextItem> items = result.evidence().stream()
                .filter(e -> e.score() >= minScore)
                .map(this::toContextItem)
                .toList();

        return new ContextContribution(SOURCE_ID, priority, items);
    }

    private ContextItem toContextItem(KnowledgeEvidence evidence) {
        String content = evidence.item() != null ? evidence.item().content() : "";
        String itemId = evidence.item() != null ? evidence.item().id() : "know-" + System.nanoTime();

        return new ContextItem(
                "know-" + itemId,
                ContextKind.KNOWLEDGE,
                content,
                ContextSource.of(SOURCE_ID, "knowledge-resolver"),
                priority,
                Map.of(
                        "score", evidence.score(),
                        "retrievalMethod", evidence.retrievalMethod() != null
                                ? evidence.retrievalMethod() : "unknown"
                )
        );
    }
}
