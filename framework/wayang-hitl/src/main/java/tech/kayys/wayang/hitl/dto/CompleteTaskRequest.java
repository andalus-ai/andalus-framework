package tech.kayys.wayang.hitl.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

/**
 * Represents a complete task request.
 *
 * <p>Its components capture `outcome`, `comments`, `data`.</p>
 *
 * @param outcome the outcome
 * @param comments the comments
 * @param data the data
 */


public record CompleteTaskRequest(
                String outcome,
                String comments,
                @NotNull Map<String, Object> data) {
}