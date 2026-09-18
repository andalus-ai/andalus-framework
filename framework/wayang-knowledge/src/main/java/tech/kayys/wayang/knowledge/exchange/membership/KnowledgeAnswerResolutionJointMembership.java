package tech.kayys.wayang.knowledge.exchange.membership;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Represents a knowledge answer resolution joint membership.
 *
 * <p>Its components capture `transition id`, `old epoch id`, `new epoch id`, `old runtime ids`, `new runtime ids`, and other values.</p>
 *
 * @param transitionId the transition id
 * @param oldEpochId the old epoch id
 * @param newEpochId the new epoch id
 * @param oldRuntimeIds the old runtime ids
 * @param newRuntimeIds the new runtime ids
 * @param oldQuorum the old quorum
 * @param newQuorum the new quorum
 * @param effectiveFrom the effective from
 * @param effectiveUntil the effective until
 */


public record KnowledgeAnswerResolutionJointMembership(
        String transitionId,
        String oldEpochId,
        String newEpochId,
        List<String> oldRuntimeIds,
        List<String> newRuntimeIds,
        int oldQuorum,
        int newQuorum,
        Instant effectiveFrom,
        Instant effectiveUntil
) {
    public KnowledgeAnswerResolutionJointMembership {
        Objects.requireNonNull(transitionId, "transitionId");
        Objects.requireNonNull(oldEpochId, "oldEpochId");
        Objects.requireNonNull(newEpochId, "newEpochId");
        Objects.requireNonNull(oldRuntimeIds, "oldRuntimeIds");
        Objects.requireNonNull(newRuntimeIds, "newRuntimeIds");
        Objects.requireNonNull(effectiveFrom, "effectiveFrom");

        oldRuntimeIds = List.copyOf(oldRuntimeIds);
        newRuntimeIds = List.copyOf(newRuntimeIds);

        if (oldQuorum < 1 || newQuorum < 1) {
            throw new IllegalArgumentException("quorum must be positive");
        }

        if (oldQuorum > oldRuntimeIds.size() || newQuorum > newRuntimeIds.size()) {
            throw new IllegalArgumentException("quorum exceeds membership size");
        }
    }

    public boolean activeAt(Instant instant) {
        return !instant.isBefore(effectiveFrom)
                && (effectiveUntil == null || instant.isBefore(effectiveUntil));
    }

    public boolean contains(String runtimeId) {
        return oldRuntimeIds.contains(runtimeId) || newRuntimeIds.contains(runtimeId);
    }
}
