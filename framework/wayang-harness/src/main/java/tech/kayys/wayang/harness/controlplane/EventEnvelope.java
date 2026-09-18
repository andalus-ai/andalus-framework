package tech.kayys.wayang.harness.controlplane;

import tech.kayys.wayang.harness.authority.CoordinationEpoch;
import tech.kayys.wayang.harness.journal.EventSequence;

import java.util.Objects;

/**
 * Standard envelope wrapping a control-plane event with authority epoch and stream sequence.
 */
public record EventEnvelope(
        WayangEvent event,
        CoordinationEpoch epoch,
        EventSequence sequence
) {

    public EventEnvelope {
        Objects.requireNonNull(event, "WayangEvent cannot be null");
        epoch = epoch != null ? epoch : CoordinationEpoch.initial();
        sequence = sequence != null ? sequence : EventSequence.initial();
    }

    public static EventEnvelope of(WayangEvent event, CoordinationEpoch epoch, EventSequence sequence) {
        return new EventEnvelope(event, epoch, sequence);
    }
}
