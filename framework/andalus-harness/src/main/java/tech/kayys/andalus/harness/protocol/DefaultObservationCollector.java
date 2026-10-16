package tech.kayys.andalus.harness.protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public final class DefaultObservationCollector
        implements ObservationCollector {

    private final List<TurnObservation> observations =
            new ArrayList<>();

    private final AtomicLong sequence =
            new AtomicLong();

    @Override
    public synchronized void record(
            TurnObservation observation
    ) {
        observations.add(observation);
    }

    @Override
    public synchronized ObservationSet snapshot() {
        return new ObservationSet(
                List.copyOf(observations)
        );
    }

    @Override
    public long nextSequence() {
        return sequence.getAndIncrement();
    }
}
