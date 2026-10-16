package tech.kayys.andalus.harness.protocol;

import java.util.List;

public record ObservationSet(
        List<TurnObservation> observations
) {

    public ObservationSet {
        observations = observations != null
                ? List.copyOf(observations)
                : List.of();
    }

    public static ObservationSet empty() {
        return new ObservationSet(List.of());
    }

    public static ObservationSet of(TurnObservation... observations) {
        return new ObservationSet(List.of(observations));
    }

    public boolean isEmpty() {
        return observations.isEmpty();
    }

    public int size() {
        return observations.size();
    }

    public List<TurnObservation> ofType(
            ObservationType type
    ) {
        return observations.stream()
                .filter(o -> o.type() == type)
                .toList();
    }
}
