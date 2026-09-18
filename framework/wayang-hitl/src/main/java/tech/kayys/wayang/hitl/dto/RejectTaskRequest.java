package tech.kayys.wayang.hitl.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

/**
 * Represents a reject task request.
 *
 * <p>Its components capture `reason`, `data`.</p>
 *
 * @param reason the reason
 * @param data the data
 */


public record RejectTaskRequest(
        @NotBlank String reason,
        Map<String, Object> data) {
}