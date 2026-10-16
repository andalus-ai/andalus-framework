package tech.kayys.andalus.knowledge.streaming;

import tech.kayys.andalus.knowledge.*;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletionStage;

/**
 * Bridges KnowledgeResolver into the Tool calling system.
 * Knowledge retrieval surfaces naturally in tool-call traces,
 * matching the ergonomics of OpenAI Agents SDK tool calling.
 */
public final class KnowledgeAsToolBridge {

    private final KnowledgeResolver resolver;
    private final KnowledgeContextFactory contextFactory;

    public KnowledgeAsToolBridge(
            KnowledgeResolver resolver,
            KnowledgeContextFactory contextFactory) {
        this.resolver = Objects.requireNonNull(resolver, "resolver");
        this.contextFactory = Objects.requireNonNull(contextFactory, "contextFactory");
    }

    /**
     * Execute a knowledge retrieval as if it were a tool call.
     * The invocation participates in tool-call tracing.
     */
    public CompletionStage<KnowledgeResult> retrieve(
            String query,
            KnowledgeRuntimeContext runtimeContext,
            KnowledgeInvocation invocation) {

        Objects.requireNonNull(query, "query");
        Objects.requireNonNull(runtimeContext, "runtimeContext");

        KnowledgeContext ctx = contextFactory.create(runtimeContext);
        KnowledgeQuery kq = KnowledgeQuery.of(query);

        return resolver.resolve(kq, ctx)
                .thenApply(result -> {
                    // enrich result metadata with tool-call tracing fields
                    Map<String, Object> enriched = new java.util.HashMap<>(result.metadata());
                    if (invocation != null) {
                        enriched.put("tool.invocationId", invocation.invocationId());
                        enriched.put("tool.executionId", invocation.executionId());
                        enriched.put("tool.traceId", invocation.traceId());
                    }
                    enriched.put("tool.name", "knowledge_retrieve");
                    enriched.put("tool.query", query);
                    return new KnowledgeResult(result.query(), result.evidence(),
                            Map.copyOf(enriched), result.timestamp());
                });
    }

    /** Simplified call without invocation tracking */
    public CompletionStage<KnowledgeResult> retrieve(String query, KnowledgeRuntimeContext runtimeContext) {
        return retrieve(query, runtimeContext, null);
    }
}
