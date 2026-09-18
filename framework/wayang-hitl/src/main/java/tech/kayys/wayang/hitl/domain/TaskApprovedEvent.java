package tech.kayys.wayang.hitl.domain;

import java.time.Instant;
import java.util.Map;

/**
 * Represents a task approved event.
 *
 * <p>Its components capture `task id`, `approved by`, `data`, `comments`, `occurred at`.</p>
 *
 * @param taskId the task id
 * @param approvedBy the approved by
 * @param data the data
 * @param comments the comments
 * @param occurredAt the occurred at
 */


public record TaskApprovedEvent(
        HumanTaskId taskId,
        String approvedBy,
        Map<String, Object> data,
        String comments,
        Instant occurredAt) implements HumanTaskEvent {
}