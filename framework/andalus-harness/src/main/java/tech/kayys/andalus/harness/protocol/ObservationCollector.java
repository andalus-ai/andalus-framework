package tech.kayys.andalus.harness.protocol;

public interface ObservationCollector {

    void record(TurnObservation observation);

    ObservationSet snapshot();

    default void record(
            TurnId turnId,
            ObservationType type,
            Object value
    ) {
        record(new TurnObservation(
                ObservationId.generate(),
                turnId,
                nextSequence(),
                type,
                java.time.Instant.now(),
                value,
                java.util.Map.of()
        ));
    }

    long nextSequence();
}
