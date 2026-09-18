package tech.kayys.wayang.knowledge.exchange.membership;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Represents a knowledge answer resolution membership set.
 *
 * <p>Its components capture `epoch id`, `sequence`, `members`, `quorum`, `participant set fingerprint`, and other values.</p>
 *
 * @param epochId the epoch id
 * @param sequence the sequence
 * @param members the members
 * @param quorum the quorum
 * @param participantSetFingerprint the participant set fingerprint
 * @param effectiveFrom the effective from
 * @param effectiveUntil the effective until
 */


public record KnowledgeAnswerResolutionMembershipSet(
        String epochId,
        long sequence,
        List<KnowledgeAnswerResolutionMembership> members,
        int quorum,
        String participantSetFingerprint,
        Instant effectiveFrom,
        Instant effectiveUntil
) {
    public KnowledgeAnswerResolutionMembershipSet {
        Objects.requireNonNull(epochId, "epochId");
        Objects.requireNonNull(members, "members");
        Objects.requireNonNull(participantSetFingerprint, "participantSetFingerprint");
        Objects.requireNonNull(effectiveFrom, "effectiveFrom");

        members = List.copyOf(members);

        if (sequence < 0) {
            throw new IllegalArgumentException("sequence must be >= 0");
        }
        if (quorum < 1) {
            throw new IllegalArgumentException("quorum must be >= 1");
        }
        if (quorum > members.size()) {
            throw new IllegalArgumentException("quorum cannot exceed member count");
        }
    }

    public boolean activeAt(Instant instant) {
        return !instant.isBefore(effectiveFrom)
                && (effectiveUntil == null || instant.isBefore(effectiveUntil));
    }
}
