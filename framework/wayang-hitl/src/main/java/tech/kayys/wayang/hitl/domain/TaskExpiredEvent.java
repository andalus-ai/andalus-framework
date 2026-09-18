package tech.kayys.wayang.hitl.domain;

import java.time.Instant;

/**
 * Represents a task expired event.
 *
 * <p>Its components capture `task id`, `occurred at`.</p>
 *
 * @param taskId the task id
 * @param occurredAt the occurred at
 */


public record TaskExpiredEvent(
        HumanTaskId taskId,
        Instant occurredAt) implements HumanTaskEvent {
}