package tech.kayys.wayang.hitl.domain;

import java.time.Instant;

/**
 * Represents a task cancelled event.
 *
 * <p>Its components capture `task id`, `cancelled by`, `reason`, `occurred at`.</p>
 *
 * @param taskId the task id
 * @param cancelledBy the cancelled by
 * @param reason the reason
 * @param occurredAt the occurred at
 */


public record TaskCancelledEvent(
        HumanTaskId taskId,
        String cancelledBy,
        String reason,
        Instant occurredAt) implements HumanTaskEvent {
}