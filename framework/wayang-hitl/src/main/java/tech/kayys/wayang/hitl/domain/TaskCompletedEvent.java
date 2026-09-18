package tech.kayys.wayang.hitl.domain;

import java.time.Instant;
import java.util.Map;

/**
 * Represents a task completed event.
 *
 * <p>Its components capture `task id`, `completed by`, `outcome`, `data`, `comments`, and other values.</p>
 *
 * @param taskId the task id
 * @param completedBy the completed by
 * @param outcome the outcome
 * @param data the data
 * @param comments the comments
 * @param occurredAt the occurred at
 */


public record TaskCompletedEvent(
        HumanTaskId taskId,
        String completedBy,
        TaskOutcome outcome,
        Map<String, Object> data,
        String comments,
        Instant occurredAt) implements HumanTaskEvent {
}