package tech.kayys.wayang.hitl.domain;

import java.time.Instant;

/**
 * Represents a task assigned event.
 *
 * <p>Its components capture `task id`, `assignment`, `occurred at`.</p>
 *
 * @param taskId the task id
 * @param assignment the assignment
 * @param occurredAt the occurred at
 */


public record TaskAssignedEvent(
        HumanTaskId taskId,
        TaskAssignment assignment,
        Instant occurredAt) implements HumanTaskEvent {
}