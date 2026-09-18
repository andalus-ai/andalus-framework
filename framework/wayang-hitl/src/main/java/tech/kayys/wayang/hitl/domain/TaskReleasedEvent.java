package tech.kayys.wayang.hitl.domain;

import java.time.Instant;

/**
 * Represents a task released event.
 *
 * <p>Its components capture `task id`, `released by`, `occurred at`.</p>
 *
 * @param taskId the task id
 * @param releasedBy the released by
 * @param occurredAt the occurred at
 */


public record TaskReleasedEvent(
        HumanTaskId taskId,
        String releasedBy,
        Instant occurredAt) implements HumanTaskEvent {
}