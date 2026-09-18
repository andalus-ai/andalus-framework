package tech.kayys.wayang.hitl.dto;

import java.time.Instant;

/**
 * Represents a audit entry dto.
 *
 * <p>Its components capture `entry id`, `action`, `details`, `performed by`, `timestamp`.</p>
 *
 * @param entryId the entry id
 * @param action the action
 * @param details the details
 * @param performedBy the performed by
 * @param timestamp the timestamp
 */


public record AuditEntryDto(
        String entryId,
        String action,
        String details,
        String performedBy,
        Instant timestamp) {
}