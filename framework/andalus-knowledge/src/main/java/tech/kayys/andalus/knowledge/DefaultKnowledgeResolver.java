package tech.kayys.andalus.knowledge;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public final class DefaultKnowledgeResolver implements KnowledgeResolver {

    private final KnowledgeRegistry registry;
    private final KnowledgeResolutionPolicy policy;
    private final KnowledgeSelector selector;
    private final KnowledgeBudget budget;

    public DefaultKnowledgeResolver(
            KnowledgeRegistry registry,
            KnowledgeResolutionPolicy policy,
            KnowledgeSelector selector,
            KnowledgeBudget budget) {
        this.registry = Objects.requireNonNull(registry, "registry");
        this.policy = policy != null ? policy : new AllowAllKnowledgeResolutionPolicy();
        this.selector = Objects.requireNonNull(selector, "selector");
        this.budget = budget != null ? budget : KnowledgeBudget.defaults();
    }

    /** Convenience constructor with allow-all policy and default budget */
    public DefaultKnowledgeResolver(KnowledgeRegistry registry, KnowledgeSelector selector) {
        this(registry, new AllowAllKnowledgeResolutionPolicy(), selector, KnowledgeBudget.defaults());
    }

    @Override
    public CompletionStage<KnowledgeResult> resolve(KnowledgeQuery query, KnowledgeContext context) {
        Objects.requireNonNull(query, "query");
        long startMs = System.currentTimeMillis();

        KnowledgeContext effectiveContext = context == null ? KnowledgeContext.empty() : context;
        KnowledgeResolutionRequest request = KnowledgeResolutionRequest.of(query, effectiveContext);

        // 1. determine candidate sources
        List<KnowledgeSource> candidates = registry.resolve(effectiveContext);

        // 2. filter unhealthy
        List<KnowledgeSource> healthy = candidates.stream()
                .filter(KnowledgeSource::isHealthy)
                .toList();

        List<String> unhealthyIds = candidates.stream()
                .filter(s -> !s.isHealthy())
                .map(KnowledgeSource::id)
                .toList();

        // 3. policy check concurrently
        List<CompletableFuture<Map.Entry<KnowledgeSource, KnowledgeResolutionDecision>>> policyChecks =
                healthy.stream()
                        .map(source -> policy.evaluate(request, source)
                                .toCompletableFuture()
                                .thenApply(decision -> Map.entry(source, decision)))
                        .toList();

        return CompletableFuture.allOf(policyChecks.toArray(new CompletableFuture[0]))
                .thenCompose(ignored -> {
                    List<KnowledgeSource> allowed = new ArrayList<>();
                    List<String> denied = new ArrayList<>();

                    for (var future : policyChecks) {
                        var entry = future.join();
                        if (entry.getValue().allowed()) {
                            allowed.add(entry.getKey());
                        } else {
                            denied.add(entry.getKey().id());
                        }
                    }

                    // 4. query allowed sources concurrently; capture failures as diagnostics
                    List<CompletableFuture<KnowledgeResult>> queries = allowed.stream()
                            .map(source -> source.query(query, effectiveContext)
                                    .toCompletableFuture()
                                    .exceptionally(ex -> {
                                        // source failure → empty result with failure metadata
                                        Map<String, Object> failMeta = Map.of(
                                                "sourceFailure", new KnowledgeSourceFailure(
                                                        source.id(),
                                                        KnowledgeSourceFailure.CODE_QUERY_FAILED,
                                                        ex.getMessage() == null ? "Unknown error" : ex.getMessage()));
                                        return new KnowledgeResult(query, List.of(), failMeta, Instant.now());
                                    }))
                            .toList();

                    return CompletableFuture.allOf(queries.toArray(new CompletableFuture[0]))
                            .thenApply(v -> {
                                List<KnowledgeEvidence> allEvidence = new ArrayList<>();
                                List<KnowledgeSourceFailure> failures = new ArrayList<>();

                                for (var q : queries) {
                                    KnowledgeResult r = q.join();
                                    // detect failure results
                                    if (r.metadata().containsKey("sourceFailure")) {
                                        failures.add((KnowledgeSourceFailure) r.metadata().get("sourceFailure"));
                                    } else {
                                        allEvidence.addAll(r.evidence());
                                    }
                                }

                                // 5. mandatory safety: filter items invalid at asOf
                                Instant asOf = effectiveContext.asOf();
                                List<KnowledgeEvidence> safeEvidence = allEvidence.stream()
                                        .filter(e -> e.item() != null && e.item().isCurrentlyValid(asOf))
                                        .toList();

                                // 6. delegate to KnowledgeSelector
                                KnowledgeSelectionResult selection =
                                        selector.select(safeEvidence, budget, effectiveContext);

                                long durationMs = System.currentTimeMillis() - startMs;

                                // 7. build result metadata
                                Map<String, Object> meta = new HashMap<>();
                                meta.put("sources.selected", allowed.stream().map(KnowledgeSource::id).toList());
                                meta.put("sources.denied", denied);
                                meta.put("sources.unhealthy", unhealthyIds);
                                meta.put("sources.failed", failures.stream().map(KnowledgeSourceFailure::sourceId).toList());
                                meta.put("evidence.candidates", safeEvidence.size());
                                meta.put("evidence.selected", selection.selected().size());
                                meta.put("resolution.durationMillis", durationMs);
                                meta.put("resolution.partial", !failures.isEmpty());

                                return new KnowledgeResult(query, selection.selected(), Map.copyOf(meta), Instant.now());
                            });
                });
    }
}
