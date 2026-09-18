package tech.kayys.wayang.harness.scheduling.scheduler;

import java.util.Optional;

/**
 * Universal contract for durable execution scheduling and wake management.
 */
public interface Scheduler {

    ScheduleHandle schedule(ScheduleRequest request);

    void cancel(ScheduleId scheduleId);

    Optional<ScheduleHandle> get(ScheduleId scheduleId);
}
