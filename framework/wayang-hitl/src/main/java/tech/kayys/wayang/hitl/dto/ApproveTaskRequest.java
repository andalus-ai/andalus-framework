package tech.kayys.wayang.hitl.dto;

import java.util.Map;

/**
 * Represents a approve task request.
 *
 * <p>Its components capture `comments`, `data`.</p>
 *
 * @param comments the comments
 * @param data the data
 */


public record ApproveTaskRequest(
        String comments,
        Map<String, Object> data) {
}