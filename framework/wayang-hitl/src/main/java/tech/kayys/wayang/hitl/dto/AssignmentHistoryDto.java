package tech.kayys.wayang.hitl.dto;

import java.time.Instant;

/**
 * Represents a assignment history dto.
 *
 * <p>Its components capture `assignee type`, `assignee identifier`, `assigned by`, `assigned at`, `delegation reason`.</p>
 *
 * @param assigneeType the assignee type
 * @param assigneeIdentifier the assignee identifier
 * @param assignedBy the assigned by
 * @param assignedAt the assigned at
 * @param delegationReason the delegation reason
 */


public record AssignmentHistoryDto(
        String assigneeType,
        String assigneeIdentifier,
        String assignedBy,
        Instant assignedAt,
        String delegationReason) {
}