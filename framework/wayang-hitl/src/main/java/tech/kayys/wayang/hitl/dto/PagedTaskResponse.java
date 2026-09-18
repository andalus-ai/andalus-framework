package tech.kayys.wayang.hitl.dto;

import java.util.List;

/**
 * Represents a paged task response.
 *
 * <p>Its components capture `tasks`, `page`, `size`, `total elements`, `total pages`.</p>
 *
 * @param tasks the tasks
 * @param page the page
 * @param size the size
 * @param totalElements the total elements
 * @param totalPages the total pages
 */


public record PagedTaskResponse(
        List<TaskDto> tasks,
        int page,
        int size,
        long totalElements,
        int totalPages) {
}