package tech.kayys.andalus.harness.decision.trace;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Default in-memory implementation of {@link DecisionTraceRuntime}.
 */
public class DefaultDecisionTraceRuntime implements DecisionTraceRuntime {

    private final Map<String, DecisionTrace> tracesById = new ConcurrentHashMap<>();

    @Override
    public CompletionStage<DecisionTrace> start(DecisionTraceRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        String id = "dtrace-" + UUID.randomUUID();
        String traceId = (request.traceId() != null && !request.traceId().isBlank())
                ? request.traceId()
                : "trace-" + UUID.randomUUID();

        DecisionTrace trace = new DecisionTrace(
                id,
                request.executionId(),
                request.turnId(),
                traceId,
                0L,
                request.contextSnapshotReference(),
                request.stateRevision(),
                request.goalId(),
                request.taskId(),
                request.strategyId(),
                DecisionTraceStatus.IN_PROGRESS,
                List.of(),
                Instant.now(),
                Optional.empty(),
                request.metadata()
        );

        tracesById.put(id, trace);
        return CompletableFuture.completedFuture(trace);
    }

    @Override
    public CompletionStage<DecisionTrace> append(DecisionTraceAppendRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        DecisionTrace current = tracesById.get(request.decisionTraceId());
        if (current == null) {
            return CompletableFuture.failedFuture(
                    new IllegalArgumentException("Decision trace not found: " + request.decisionTraceId()));
        }

        if (current.status() == DecisionTraceStatus.COMPLETED ||
            current.status() == DecisionTraceStatus.CANCELLED) {
            return CompletableFuture.failedFuture(
                    new IllegalStateException("Cannot append to terminal decision trace: " + current.id() + " (status=" + current.status() + ")"));
        }

        // Deduplication check by entry ID
        boolean duplicate = current.entries().stream()
                .anyMatch(e -> e.id().equals(request.entry().id()));
        if (duplicate) {
            return CompletableFuture.completedFuture(current);
        }

        DecisionTrace updated = current.withEntry(request.entry());
        tracesById.put(updated.id(), updated);
        return CompletableFuture.completedFuture(updated);
    }

    @Override
    public CompletionStage<DecisionTrace> complete(DecisionTraceCompletionRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        DecisionTrace current = tracesById.get(request.decisionTraceId());
        if (current == null) {
            return CompletableFuture.failedFuture(
                    new IllegalArgumentException("Decision trace not found: " + request.decisionTraceId()));
        }

        if (current.status() == DecisionTraceStatus.COMPLETED) {
            return CompletableFuture.completedFuture(current);
        }

        DecisionTrace completed = current.complete(
                request.status(),
                request.outcomeReference(),
                request.metadata()
        );
        tracesById.put(completed.id(), completed);
        return CompletableFuture.completedFuture(completed);
    }

    @Override
    public CompletionStage<Optional<DecisionTrace>> get(DecisionTraceQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        if (query.decisionTraceId().isPresent()) {
            return CompletableFuture.completedFuture(
                    Optional.ofNullable(tracesById.get(query.decisionTraceId().get())));
        }

        return queryTraces(query).thenApply(list -> list.isEmpty() ? Optional.empty() : Optional.of(list.get(0)));
    }

    @Override
    public CompletionStage<List<DecisionTrace>> queryTraces(DecisionTraceQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        List<DecisionTrace> matching = tracesById.values().stream()
                .filter(t -> query.decisionTraceId().map(id -> t.id().equals(id)).orElse(true))
                .filter(t -> query.executionId().map(eid -> t.executionId().equals(eid)).orElse(true))
                .filter(t -> query.turnId().map(tid -> t.turnId().equals(tid)).orElse(true))
                .collect(Collectors.toList());

        return CompletableFuture.completedFuture(matching);
    }
}
