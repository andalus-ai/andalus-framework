package tech.kayys.andalus.harness.planning;

import java.util.Objects;

/**
 * Requests one stable page of a plan's revision history.
 *
 * <p>The cursor is exclusive and represents the last observed {@code toRevision}
 * from the preceding page. Pages are ordered by ascending {@code toRevision}.</p>
 */
public record PlanRevisionPageRequest(
        PlanRevisionQuery query,
        int pageSize,
        Long afterRevisionExclusive
) {
    public static final int MAX_PAGE_SIZE = 500;

    public PlanRevisionPageRequest {
        Objects.requireNonNull(query, "query is required");
        if (pageSize < 1 || pageSize > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("pageSize must be between 1 and " + MAX_PAGE_SIZE);
        }
        if (afterRevisionExclusive != null && afterRevisionExclusive < 0) {
            throw new IllegalArgumentException("afterRevisionExclusive must be non-negative");
        }
    }

    public static PlanRevisionPageRequest firstPage(PlanRevisionQuery query, int pageSize) {
        return new PlanRevisionPageRequest(query, pageSize, null);
    }
}
