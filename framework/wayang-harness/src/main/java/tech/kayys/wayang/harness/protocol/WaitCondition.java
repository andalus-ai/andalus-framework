package tech.kayys.wayang.harness.protocol;

import java.time.Duration;
import java.util.Optional;

/**
 * Represents a wait condition.
 *
 * <p>Its components capture `type`, `timeout`, `event type`.</p>
 *
 * @param type the type
 * @param timeout the timeout
 * @param eventType the event type
 */


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
