package tech.kayys.andalus.harness.planning;

import java.util.List;
import java.util.Objects;

/**
 * Append-only storage contract for accepted plan revisions.
 *
 * <p>Implementations should preserve append order and reject duplicate transitions
 * where appropriate. Durable implementations belong to the runtime/infrastructure layer.</p>
 */
public interface PlanRevisionHistory {

    /** Append an accepted revision record. */
    void append(PlanRevisionRecord record);

    /** Return the revision records for a plan in ascending revision order. */
    List<PlanRevisionRecord> revisions(PlanId planId);

    /**
     * Query revision records for a single plan. The default implementation is
     * storage-neutral and filters the ordered result of {@link #revisions(PlanId)}.
     * Persistent implementations may override this method to push filters down
     * into their storage engine.
     */
    default List<PlanRevisionRecord> search(PlanRevisionQuery query) {
        Objects.requireNonNull(query, "query is required");
        return revisions(query.planId()).stream()
                .filter(record -> query.fromRevisionInclusive() == null
                        || record.toRevision() >= query.fromRevisionInclusive())
                .filter(record -> query.toRevisionInclusive() == null
                        || record.toRevision() <= query.toRevisionInclusive())
                .filter(record -> query.revisionPolicy() == null
                        || record.revisionPolicy() == query.revisionPolicy())
                .filter(record -> query.completedWorkReplaced() == null
                        || (!record.diff().replacedCompletedStepIds().isEmpty())
                                == query.completedWorkReplaced())
                .toList();
    }

    /**
     * Return one stable page of revision records.
     *
     * <p>The default implementation filters the ordered search result in memory.
     * Storage-backed implementations should override this method to apply the
     * query, exclusive revision cursor, and page limit in the storage engine.
     * The cursor is the last returned {@code toRevision}; callers pass it back
     * as {@code afterRevisionExclusive} to continue without repeating records.</p>
     */
    default PlanRevisionPage page(PlanRevisionPageRequest request) {
        Objects.requireNonNull(request, "request is required");
        List<PlanRevisionRecord> matching = search(request.query()).stream()
                .filter(record -> request.afterRevisionExclusive() == null
                        || record.toRevision() > request.afterRevisionExclusive())
                .limit((long) request.pageSize() + 1)
                .toList();
        boolean hasMore = matching.size() > request.pageSize();
        List<PlanRevisionRecord> pageRecords = hasMore
                ? matching.subList(0, request.pageSize()) : matching;
        Long nextCursor = hasMore
                ? pageRecords.get(pageRecords.size() - 1).toRevision() : null;
        return new PlanRevisionPage(pageRecords, nextCursor, hasMore);
    }

}
