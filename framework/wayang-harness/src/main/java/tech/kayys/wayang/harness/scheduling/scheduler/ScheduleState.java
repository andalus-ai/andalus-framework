package tech.kayys.wayang.harness.scheduling.scheduler;

/**
 * Operating states of a managed schedule.
 */
public enum ScheduleState {
    CREATED,
    SCHEDULED,
    FIRED,
    DISPATCHED,
    COMPLETED,
    CANCELED,
    EXPIRED,
    FAILED
}
