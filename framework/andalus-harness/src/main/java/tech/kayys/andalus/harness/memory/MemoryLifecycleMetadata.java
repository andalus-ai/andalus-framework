package tech.kayys.andalus.harness.memory;

import java.time.Instant;
import java.util.Optional;

/**
 * Metadata capturing the lifecycle transitions and revisions of a memory entry.
 */
public record MemoryLifecycleMetadata(
        MemoryLifecycleState state,
        Instant createdAt,
        Instant activatedAt,
        Optional<Instant> expiresAt,
        Optional<Instant> archivedAt,
        Optional<Instant> deletedAt,
        Optional<String> archiveReason,
        Optional<String> deletionReason,
        long revision
) {

    public MemoryLifecycleMetadata {
        state = state == null ? MemoryLifecycleState.CANDIDATE : state;
        createdAt = createdAt == null ? Instant.now() : createdAt;
        activatedAt = activatedAt == null ? createdAt : activatedAt;
        expiresAt = expiresAt == null ? Optional.empty() : expiresAt;
        archivedAt = archivedAt == null ? Optional.empty() : archivedAt;
        deletedAt = deletedAt == null ? Optional.empty() : deletedAt;
        archiveReason = archiveReason == null ? Optional.empty() : archiveReason;
        deletionReason = deletionReason == null ? Optional.empty() : deletionReason;
        if (revision < 0) {
            revision = 1L;
        }
    }

    public static MemoryLifecycleMetadata active() {
        Instant now = Instant.now();
        return new MemoryLifecycleMetadata(
                MemoryLifecycleState.ACTIVE,
                now,
                now,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                1L
        );
    }

    public MemoryLifecycleMetadata withExpired(Instant expiredAt) {
        return new MemoryLifecycleMetadata(
                MemoryLifecycleState.EXPIRED,
                createdAt,
                activatedAt,
                Optional.ofNullable(expiredAt),
                archivedAt,
                deletedAt,
                archiveReason,
                deletionReason,
                revision + 1
        );
    }

    public MemoryLifecycleMetadata withArchived(String reason) {
        return new MemoryLifecycleMetadata(
                MemoryLifecycleState.ARCHIVED,
                createdAt,
                activatedAt,
                expiresAt,
                Optional.of(Instant.now()),
                deletedAt,
                Optional.ofNullable(reason),
                deletionReason,
                revision + 1
        );
    }
}
