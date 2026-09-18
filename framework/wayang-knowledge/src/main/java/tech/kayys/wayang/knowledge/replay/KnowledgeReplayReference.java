package tech.kayys.wayang.knowledge.replay;

/**
 * Represents a knowledge replay reference.
 *
 * <p>Its components capture `snapshot id`, `trace id`, `fingerprint`.</p>
 *
 * @param snapshotId the snapshot id
 * @param traceId the trace id
 * @param fingerprint the fingerprint
 */


public record KnowledgeReplayReference(
        String snapshotId,
        String traceId,
        String fingerprint
) {}
