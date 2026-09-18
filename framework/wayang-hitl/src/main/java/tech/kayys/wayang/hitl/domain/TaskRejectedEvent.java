package tech.kayys.wayang.hitl.domain;

import java.time.Instant;
import java.util.Map;

/**
 * Represents a task rejected event.
 *
 * <p>Its components capture `task id`, `rejected by`, `reason`, `data`, `occurred at`.</p>
 *
 * @param taskId the task id
 * @param rejectedBy the rejected by
 * @param reason the reason
 * @param data the data
 * @param occurredAt the occurred at
 */


public record TaskRejectedEvent(
        HumanTaskId taskId,
        String rejectedBy,
        String reason,
        Map<String, Object> data,
        Instant occurredAt) implements HumanTaskEvent {
}