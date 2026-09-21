package tech.kayys.andalus.hitl.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Represents a add comment request.
 *
 * <p>Its components capture `comment`.</p>
 *
 * @param comment the comment
 */


public record AddCommentRequest(
        @NotBlank String comment) {
}