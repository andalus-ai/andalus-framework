package tech.kayys.wayang.hitl.dto;

import java.time.Instant;
import java.util.Map;

/**
 * Represents a task dto.
 *
 * <p>Its components capture `task id`, `workflow run id`, `node id`, `task type`, `title`, and other values.</p>
 *
 * @param taskId the task id
 * @param workflowRunId the workflow run id
 * @param nodeId the node id
 * @param taskType the task type
 * @param title the title
 * @param description the description
 * @param priority the priority
 * @param status the status
 * @param assigneeType the assignee type
 * @param assigneeIdentifier the assignee identifier
 * @param assignedBy the assigned by
 * @param createdAt the created at
 * @param claimedAt the claimed at
 * @param completedAt the completed at
 * @param dueDate the due date
 * @param outcome the outcome
 * @param completedBy the completed by
 * @param comments the comments
 * @param formData the form data
 * @param escalated the escalated
 * @param escalatedTo the escalated to
 */


public record TaskDto(
                String taskId,
                String workflowRunId,
                String nodeId,
                String taskType,
                String title,
                String description,
                int priority,
                String status,
                String assigneeType,
                String assigneeIdentifier,
                String assignedBy,
                Instant createdAt,
                Instant claimedAt,
                Instant completedAt,
                Instant dueDate,
                String outcome,
                String completedBy,
                String comments,
                Map<String, Object> formData,
                boolean escalated,
                String escalatedTo) {
}