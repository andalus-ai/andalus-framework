package tech.kayys.wayang.harness.protocol;

import java.time.Duration;
import java.util.Optional;

public record WaitCondition(
        String type,
        Optional<Duration> timeout,
        Optional<String> eventType
) {
    public static WaitCondition forDuration(Duration duration) {
        return new WaitCondition("TIMER", Optional.of(duration), Optional.empty());
    }

    public static WaitCondition forEvent(String eventType) {
        return new WaitCondition("EVENT", Optional.empty(), Optional.of(eventType));
    }
}
