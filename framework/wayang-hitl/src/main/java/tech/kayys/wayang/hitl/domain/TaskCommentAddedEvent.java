package tech.kayys.wayang.hitl.domain;

import java.time.Instant;

/**
 * Represents a task comment added event.
 *
 * <p>Its components capture `task id`, `user id`, `comment`, `occurred at`.</p>
 *
 * @param taskId the task id
 * @param userId the user id
 * @param comment the comment
 * @param occurredAt the occurred at
 */


public record TaskCommentAddedEvent(
        HumanTaskId taskId,
        String userId,
        String comment,
        Instant occurredAt) implements HumanTaskEvent {
}