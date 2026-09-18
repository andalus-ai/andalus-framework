package tech.kayys.wayang.harness.observability.replay;

import tech.kayys.wayang.harness.observability.event.EventSequence;

import java.time.Instant;
import java.util.Optional;

/**
 * Configuration options controlling state replay behavior.
 */
public record ReplayOptions(
        Optional<EventSequence> upToSequence,
        Optional<Instant> upToTimestamp,
        boolean includeIntermediateSteps
) {
    public static ReplayOptions full() {
        return new ReplayOptions(Optional.empty(), Optional.empty(), false);
    }

    public static ReplayOptions upTo(EventSequence seq) {
        return new ReplayOptions(Optional.ofNullable(seq), Optional.empty(), false);
    }
}
