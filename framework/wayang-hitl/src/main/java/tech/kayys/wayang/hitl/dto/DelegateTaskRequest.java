package tech.kayys.wayang.hitl.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Represents a delegate task request.
 *
 * <p>Its components capture `to user id`, `reason`.</p>
 *
 * @param toUserId the to user id
 * @param reason the reason
 */


public record DelegateTaskRequest(
        @NotBlank String toUserId,
        @NotBlank String reason) {
}