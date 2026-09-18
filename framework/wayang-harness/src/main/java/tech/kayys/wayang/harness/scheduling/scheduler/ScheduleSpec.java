package tech.kayys.wayang.harness.scheduling.scheduler;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * Detailed specification of when a schedule should trigger.
 */
public record ScheduleSpec(
        ScheduleType type,
        Instant at,
        Duration interval,
        String cronExpression
) {
    public static ScheduleSpec at(Instant time) {
        return new ScheduleSpec(ScheduleType.AT, Objects.requireNonNull(time, "time"), null, null);
    }

    public static ScheduleSpec after(Duration delay, Instant fromTime) {
        Objects.requireNonNull(delay, "delay");
        Instant target = (fromTime != null ? fromTime : Instant.now()).plus(delay);
        return new ScheduleSpec(ScheduleType.AFTER, target, null, null);
    }

    public static ScheduleSpec interval(Duration interval) {
        return new ScheduleSpec(ScheduleType.INTERVAL, null, Objects.requireNonNull(interval, "interval"), null);
    }

    public static ScheduleSpec cron(String expression) {
        return new ScheduleSpec(ScheduleType.CRON, null, null, Objects.requireNonNull(expression, "expression"));
    }
}
