package tech.kayys.wayang.hitl.domain;

import java.time.Instant;

/**
 * Represents a task claimed event.
 *
 * <p>Its components capture `task id`, `claimed by`, `occurred at`.</p>
 *
 * @param taskId the task id
 * @param claimedBy the claimed by
 * @param occurredAt the occurred at
 */


public record TaskClaimedEvent(
        HumanTaskId taskId,
        String claimedBy,
        Instant occurredAt) implements HumanTaskEvent {
}