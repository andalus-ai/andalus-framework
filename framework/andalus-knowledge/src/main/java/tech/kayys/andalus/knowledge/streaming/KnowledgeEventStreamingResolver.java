package tech.kayys.andalus.knowledge.streaming;

import tech.kayys.andalus.knowledge.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

/**
 * Streaming knowledge resolver that emits KnowledgeStreamingEvent items progressively.
 * Sources are queried concurrently; evidence arrives as each source completes.
 * Also implements the standard resolve() for backward compatibility.
 */
public final class KnowledgeEventStreamingResolver implements KnowledgeStreamingResolver {

    private final KnowledgeRegistry registry;
    private final KnowledgeResolutionPolicy policy;
    private final KnowledgeSelector selector;
    private final KnowledgeBudget budget;
    private final Executor executor;

    public KnowledgeEventStreamingResolver(
            KnowledgeRegistry registry,
            KnowledgeResolutionPolicy policy,
            KnowledgeSelector selector,
            KnowledgeBudget budget,
            Executor executor) {
        this.registry = Objects.requireNonNull(registry, "registry");
        this.policy = policy != null ? policy : new AllowAllKnowledgeResolutionPolicy();
        this.selector = Objects.requireNonNull(selector, "selector");
        this.budget = budget != null ? budget : KnowledgeBudget.defaults();
        this.executor = executor != null ? executor : ForkJoinPool.commonPool();
    }

    /** Standard backward-compatible resolve: collects all streaming evidence */
    @Override
    public CompletionStage<KnowledgeResult> resolve(KnowledgeQuery query, KnowledgeContext context) {
        Objects.requireNonNull(query, "query");
        KnowledgeContext ctx = context == null ? KnowledgeContext.empty() : context;
        long startMs = System.currentTimeMillis();

        List<KnowledgeSource> candidates = registry.resolve(ctx);
        KnowledgeResolutionRequest request = KnowledgeResolutionRequest.of(query, ctx);

        List<KnowledgeSource> healthy = candidates.stream().filter(KnowledgeSource::isHealthy).toList();

        // policy checks
        List<CompletableFuture<Map.Entry<KnowledgeSource, KnowledgeResolutionDecision>>> policyChecks =
                healthy.stream().map(source -> policy.evaluate(request, source)
                        .toCompletableFuture().thenApply(d -> Map.entry(source, d))).toList();

        return CompletableFuture.allOf(policyChecks.toArray(new CompletableFuture[0]))
                .thenCompose(ignored -> {
                    List<KnowledgeSource> allowed = new ArrayList<>();
                    List<String> denied = new ArrayList<>();
                    for (var f : policyChecks) {
                        var e = f.join();
                        if (e.getValue().allowed()) allowed.add(e.getKey());
                        else denied.add(e.getKey().id());
                    }

                    List<CompletableFuture<KnowledgeResult>> queries = allowed.stream()
                            .map(src -> src.query(query, ctx).toCompletableFuture()
                                    .exceptionally(ex -> new KnowledgeResult(query, List.of(),
                                            Map.of("sourceFailure", new KnowledgeSourceFailure(src.id(),
                                                    KnowledgeSourceFailure.CODE_QUERY_FAILED,
                                                    ex.getMessage() == null ? "error" : ex.getMessage())),
                                            Instant.now())))
                            .toList();

                    return CompletableFuture.allOf(queries.toArray(new CompletableFuture[0]))
                            .thenApply(v -> {
                                List<KnowledgeEvidence> all = new ArrayList<>();
                                List<KnowledgeSourceFailure> failures = new ArrayList<>();
                                for (var q : queries) {
                                    var r = q.join();
                                    if (r.metadata().containsKey("sourceFailure"))
                                        failures.add((KnowledgeSourceFailure) r.metadata().get("sourceFailure"));
                                    else all.addAll(r.evidence());
                                }
                                Instant asOf = ctx.asOf();
                                List<KnowledgeEvidence> safe = all.stream()
                                        .filter(e -> e.item() != null && e.item().isCurrentlyValid(asOf)).toList();
                                KnowledgeSelectionResult sel = selector.select(safe, budget, ctx);
                                long dur = System.currentTimeMillis() - startMs;
                                Map<String, Object> meta = new HashMap<>();
                                meta.put("sources.selected", allowed.stream().map(KnowledgeSource::id).toList());
                                meta.put("sources.denied", denied);
                                meta.put("sources.failed", failures.stream().map(KnowledgeSourceFailure::sourceId).toList());
                                meta.put("evidence.candidates", safe.size());
                                meta.put("evidence.selected", sel.selected().size());
                                meta.put("resolution.durationMillis", dur);
                                meta.put("resolution.partial", !failures.isEmpty());
                                return new KnowledgeResult(query, sel.selected(), Map.copyOf(meta), Instant.now());
                            });
                });
    }

    /** Streaming: emits evidence as each source returns results */
    @Override
    public Flow.Publisher<KnowledgeEvidence> stream(KnowledgeQuery query, KnowledgeContext context) {
        Objects.requireNonNull(query, "query");
        KnowledgeContext ctx = context == null ? KnowledgeContext.empty() : context;

        return subscriber -> {
            SubmissionPublisher<KnowledgeEvidence> publisher =
                    new SubmissionPublisher<>(executor, Flow.defaultBufferSize());
            publisher.subscribe(subscriber);

            executor.execute(() -> {
                try {
                    List<KnowledgeSource> sources = registry.resolve(ctx).stream()
                            .filter(KnowledgeSource::isHealthy).toList();

                    KnowledgeResolutionRequest req = KnowledgeResolutionRequest.of(query, ctx);
                    Instant asOf = ctx.asOf();

                    List<CompletableFuture<Void>> tasks = sources.stream().map(src ->
                            policy.evaluate(req, src).toCompletableFuture().thenCompose(decision -> {
                                if (!decision.allowed()) return CompletableFuture.completedFuture(null);
                                return src.query(query, ctx).toCompletableFuture().thenAccept(result -> {
                                    if (!publisher.isClosed()) {
                                        result.evidence().stream()
                                                .filter(e -> e.item() != null && e.item().isCurrentlyValid(asOf))
                                                .forEach(publisher::submit);
                                    }
                                }).exceptionally(ex -> null);
                            })
                    ).toList();

                    CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]))
                            .whenComplete((v, ex) -> publisher.close());
                } catch (Exception ex) {
                    publisher.closeExceptionally(ex);
                }
            });
        };
    }
}
