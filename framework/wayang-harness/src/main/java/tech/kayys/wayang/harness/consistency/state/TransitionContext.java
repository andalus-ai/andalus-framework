package tech.kayys.wayang.harness.consistency.state;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Contextual metadata supplied with an execution state transition.
 */
public record TransitionContext(
        Instant timestamp,
        String triggeredBy,
        Map<String, Object> metadata
) {

    public TransitionContext {
        timestamp = timestamp != null ? timestamp : Instant.now();
        triggeredBy = triggeredBy != null ? triggeredBy : "system";
        metadata = metadata != null ? Map.copyOf(metadata) : Map.of();
    }

    public static TransitionContext of(String triggeredBy) {
        return new TransitionContext(Instant.now(), triggeredBy, Map.of());
    }

    public static TransitionContext of(String triggeredBy, Map<String, Object> metadata) {
        return new TransitionContext(Instant.now(), triggeredBy, metadata);
    }
}
