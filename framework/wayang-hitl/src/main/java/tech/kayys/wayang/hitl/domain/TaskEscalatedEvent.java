package tech.kayys.wayang.hitl.domain;

import java.time.Instant;

/**
 * Represents a task escalated event.
 *
 * <p>Its components capture `task id`, `reason`, `escalated to`, `occurred at`.</p>
 *
 * @param taskId the task id
 * @param reason the reason
 * @param escalatedTo the escalated to
 * @param occurredAt the occurred at
 */


public record TaskEscalatedEvent(
        HumanTaskId taskId,
        EscalationReason reason,
        String escalatedTo,
        Instant occurredAt) implements HumanTaskEvent {
}