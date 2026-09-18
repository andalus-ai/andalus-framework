package tech.kayys.wayang.harness.observability.trace;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Representation of a timed causal unit of work in a trace.
 */
public record ExecutionSpan(
        SpanId id,
        TraceId traceId,
        Optional<SpanId> parentId,
        String name,
        SpanKind kind,
        Instant startedAt,
        Optional<Instant> endedAt,
        Map<String, String> attributes
) {
    public ExecutionSpan {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(traceId, "traceId");
        Objects.requireNonNull(name, "name");
        kind = kind != null ? kind : SpanKind.INTERNAL;
        startedAt = startedAt != null ? startedAt : Instant.now();
        endedAt = endedAt != null ? endedAt : Optional.empty();
        attributes = attributes != null ? Map.copyOf(attributes) : Map.of();
    }

    public static ExecutionSpan start(TraceId traceId, Optional<SpanId> parentId, String name, SpanKind kind) {
        return new ExecutionSpan(SpanId.generate(), traceId, parentId, name, kind, Instant.now(), Optional.empty(), Map.of());
    }

    public ExecutionSpan complete() {
        return new ExecutionSpan(id, traceId, parentId, name, kind, startedAt, Optional.of(Instant.now()), attributes);
    }
}
