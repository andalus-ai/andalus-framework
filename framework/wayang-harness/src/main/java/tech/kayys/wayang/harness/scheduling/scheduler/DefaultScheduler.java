package tech.kayys.wayang.harness.scheduling.scheduler;

import tech.kayys.wayang.harness.scheduling.clock.WayangClock;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe default in-memory implementation of {@link Scheduler}.
 */
public class DefaultScheduler implements Scheduler {

    private final WayangClock clock;
    private final Map<ScheduleId, ScheduleRecord> schedules = new ConcurrentHashMap<>();

    private record ScheduleRecord(
            ScheduleId id,
            ScheduleRequest request,
            ScheduleState state,
            Instant triggerTime
    ) {}

    public DefaultScheduler(WayangClock clock) {
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    @Override
    public ScheduleHandle schedule(ScheduleRequest request) {
        Objects.requireNonNull(request, "request");
        ScheduleId id = ScheduleId.generate();

        Instant triggerTime;
        if (request.specification().at() != null) {
            triggerTime = request.specification().at();
        } else if (request.specification().interval() != null) {
            triggerTime = clock.now().plus(request.specification().interval());
        } else {
            triggerTime = clock.now();
        }

        ScheduleRecord record = new ScheduleRecord(id, request, ScheduleState.SCHEDULED, triggerTime);
        schedules.put(id, record);

        return new ScheduleHandle(id, request.executionId(), ScheduleState.SCHEDULED, triggerTime);
    }

    @Override
    public void cancel(ScheduleId scheduleId) {
        if (scheduleId != null) {
            schedules.computeIfPresent(scheduleId, (k, old) ->
                    new ScheduleRecord(old.id(), old.request(), ScheduleState.CANCELED, old.triggerTime())
            );
        }
    }

    @Override
    public Optional<ScheduleHandle> get(ScheduleId scheduleId) {
        if (scheduleId == null) return Optional.empty();
        ScheduleRecord rec = schedules.get(scheduleId);
        if (rec == null) return Optional.empty();
        return Optional.of(new ScheduleHandle(rec.id(), rec.request().executionId(), rec.state(), rec.triggerTime()));
    }

    public Collection<ScheduleHandle> findDue() {
        Instant now = clock.now();
        return schedules.values().stream()
                .filter(s -> s.state() == ScheduleState.SCHEDULED && !s.triggerTime().isAfter(now))
                .map(s -> new ScheduleHandle(s.id(), s.request().executionId(), s.state(), s.triggerTime()))
                .toList();
    }
}
