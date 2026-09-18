package tech.kayys.wayang.knowledge.exchange.journal;

import java.time.Instant;
import java.util.Objects;

/**
 * Represents a knowledge answer resolution log entry.
 *
 * <p>Its components capture `index`, `term`, `epoch id`, `key fingerprint`, `consensus id`, and other values.</p>
 *
 * @param index the index
 * @param term the term
 * @param epochId the epoch id
 * @param keyFingerprint the key fingerprint
 * @param consensusId the consensus id
 * @param runtimeId the runtime id
 * @param type the type
 * @param payloadFingerprint the payload fingerprint
 * @param createdAt the created at
 * @param metadata the metadata
 */


public record KnowledgeAnswerResolutionLogEntry(
        long index,
        long term,
        String epochId,
        String keyFingerprint,
        String consensusId,
        String runtimeId,
        KnowledgeAnswerResolutionLogEntryType type,
        String payloadFingerprint,
        Instant createdAt,
        String metadata
) {
    public KnowledgeAnswerResolutionLogEntry {
        Objects.requireNonNull(epochId, "epochId");
        Objects.requireNonNull(keyFingerprint, "keyFingerprint");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(payloadFingerprint, "payloadFingerprint");
        Objects.requireNonNull(createdAt, "createdAt");

        if (index < 0) {
            throw new IllegalArgumentException("index must be >= 0");
        }
        if (term < 0) {
            throw new IllegalArgumentException("term must be >= 0");
        }
    }
}
