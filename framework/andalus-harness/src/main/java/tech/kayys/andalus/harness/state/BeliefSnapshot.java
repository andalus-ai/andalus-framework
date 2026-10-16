package tech.kayys.andalus.harness.state;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable snapshot of active, disputed, and unresolved beliefs for an execution.
 */
public record BeliefSnapshot(
        String executionId,
        long revision,
        List<Belief> beliefs,
        Instant capturedAt,
        Map<String, Object> metadata
) {
    public BeliefSnapshot {
        Objects.requireNonNull(executionId, "executionId is required");
        beliefs = beliefs == null ? List.of() : List.copyOf(beliefs);
        capturedAt = capturedAt == null ? Instant.now() : capturedAt;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public List<Belief> activeBeliefs() {
        return beliefs.stream().filter(b -> b.status() == BeliefStatus.ACTIVE).toList();
    }

    public List<Belief> disputedBeliefs() {
        return beliefs.stream().filter(b -> b.status() == BeliefStatus.DISPUTED).toList();
    }

    public static BeliefSnapshot empty(String executionId) {
        return new BeliefSnapshot(executionId, 0L, List.of(), Instant.now(), Map.of());
    }
}
