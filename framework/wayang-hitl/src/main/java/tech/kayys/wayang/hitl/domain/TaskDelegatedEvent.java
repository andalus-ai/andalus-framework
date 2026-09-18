package tech.kayys.wayang.hitl.domain;

import java.time.Instant;

/**
 * Represents a task delegated event.
 *
 * <p>Its components capture `task id`, `from user`, `to user`, `reason`, `occurred at`.</p>
 *
 * @param taskId the task id
 * @param fromUser the from user
 * @param toUser the to user
 * @param reason the reason
 * @param occurredAt the occurred at
 */


public record TaskDelegatedEvent(
        HumanTaskId taskId,
        String fromUser,
        String toUser,
        String reason,
        Instant occurredAt) implements HumanTaskEvent {
}