package tech.kayys.wayang.hitl.dto;

import java.util.List;

/**
 * Represents a task history response.
 *
 * <p>Its components capture `task id`, `audit trail`, `assignment history`.</p>
 *
 * @param taskId the task id
 * @param auditTrail the audit trail
 * @param assignmentHistory the assignment history
 */


public record TaskHistoryResponse(
        String taskId,
        List<AuditEntryDto> auditTrail,
        List<AssignmentHistoryDto> assignmentHistory) {
}