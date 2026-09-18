package tech.kayys.wayang.harness.scheduling.scheduler;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique identifier for a schedule.
 */
public record ScheduleId(String value) {
    public ScheduleId {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("ScheduleId cannot be blank");
        }
    }

    public static ScheduleId of(String value) {
        return new ScheduleId(value);
    }

    public static ScheduleId generate() {
        return new ScheduleId("sch-" + UUID.randomUUID());
    }
}
