package tech.kayys.andalus.harness.controlplane;

import tech.kayys.andalus.harness.authority.CoordinationEpoch;
import tech.kayys.andalus.harness.journal.EventSequence;

import java.util.Objects;

/**
 * Standard envelope wrapping a control-plane event with authority epoch and stream sequence.
 */
public record EventEnvelope(
        AndalusEvent event,
        CoordinationEpoch epoch,
        EventSequence sequence
) {

    public EventEnvelope {
        Objects.requireNonNull(event, "AndalusEvent cannot be null");
        epoch = epoch != null ? epoch : CoordinationEpoch.initial();
        sequence = sequence != null ? sequence : EventSequence.initial();
    }

    public static EventEnvelope of(AndalusEvent event, CoordinationEpoch epoch, EventSequence sequence) {
        return new EventEnvelope(event, epoch, sequence);
    }
}
