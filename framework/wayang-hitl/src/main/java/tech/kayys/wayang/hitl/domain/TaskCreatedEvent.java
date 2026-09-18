package tech.kayys.wayang.hitl.domain;

import java.time.Instant;

/**
 * Represents a task created event.
 *
 * <p>Its components capture `task id`, `workflow run id`, `node id`, `occurred at`.</p>
 *
 * @param taskId the task id
 * @param workflowRunId the workflow run id
 * @param nodeId the node id
 * @param occurredAt the occurred at
 */


public record TaskCreatedEvent(
        HumanTaskId taskId,
        String workflowRunId,
        String nodeId,
        Instant occurredAt) implements HumanTaskEvent {
}