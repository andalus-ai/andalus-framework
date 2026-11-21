package tech.kayys.andalus.harness.planning;

import java.util.List;
import java.util.Objects;

/**
 * One immutable page of revision history.
 *
 * @param records records in ascending {@code toRevision} order
 * @param nextCursor exclusive cursor for the next page, or {@code null} when complete
 * @param hasMore whether another page is available
 */
public record PlanRevisionPage(
        List<PlanRevisionRecord> records,
        Long nextCursor,
        boolean hasMore
) {
    public PlanRevisionPage {
        records = List.copyOf(Objects.requireNonNull(records, "records are required"));
        if (hasMore && (records.isEmpty() || nextCursor == null)) {
            throw new IllegalArgumentException("a non-final page requires records and a next cursor");
        }
        if (!hasMore && nextCursor != null) {
            throw new IllegalArgumentException("a final page must not expose a next cursor");
        }
        long previousRevision = -1;
        for (PlanRevisionRecord record : records) {
            Objects.requireNonNull(record, "page records must not contain null");
            if (record.toRevision() <= previousRevision) {
                throw new IllegalArgumentException("page records must be in ascending revision order");
            }
            previousRevision = record.toRevision();
        }
        if (hasMore && nextCursor != records.get(records.size() - 1).toRevision()) {
            throw new IllegalArgumentException("nextCursor must identify the final record in this page");
        }
    }
}
