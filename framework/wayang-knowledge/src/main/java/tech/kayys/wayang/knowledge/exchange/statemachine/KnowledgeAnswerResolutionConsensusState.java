package tech.kayys.wayang.knowledge.exchange.statemachine;

import java.time.Instant;

/**
 * Represents a knowledge answer resolution consensus state.
 *
 * <p>Its components capture `consensus id`, `key fingerprint`, `resolution fingerprint`, `dependency fingerprint`, `epoch id`, and other values.</p>
 *
 * @param consensusId the consensus id
 * @param keyFingerprint the key fingerprint
 * @param resolutionFingerprint the resolution fingerprint
 * @param dependencyFingerprint the dependency fingerprint
 * @param epochId the epoch id
 * @param status the status
 * @param activatedAt the activated at
 * @param expiresAt the expires at
 */


public record KnowledgeAnswerResolutionConsensusState(
        String consensusId,
        String keyFingerprint,
        String resolutionFingerprint,
        String dependencyFingerprint,
        String epochId,
        KnowledgeAnswerResolutionConsensusStateStatus status,
        Instant activatedAt,
        Instant expiresAt
) {}
