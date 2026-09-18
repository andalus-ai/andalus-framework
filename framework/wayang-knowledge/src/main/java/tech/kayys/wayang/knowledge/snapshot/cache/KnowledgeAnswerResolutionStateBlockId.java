package tech.kayys.wayang.knowledge.snapshot.cache;

/**
 * Represents a knowledge answer resolution state block id.
 *
 * <p>Its components capture `algorithm`, `digest`.</p>
 *
 * @param algorithm the algorithm
 * @param digest the digest
 */


public record KnowledgeAnswerResolutionStateBlockId(
        String algorithm,
        String digest
) {
    public KnowledgeAnswerResolutionStateBlockId {
        if (algorithm == null || algorithm.isBlank()) {
            throw new IllegalArgumentException("algorithm is required");
        }
        if (digest == null || digest.isBlank()) {
            throw new IllegalArgumentException("digest is required");
        }
    }

    @Override
    public String toString() {
        return algorithm.toLowerCase() + ":" + digest.toLowerCase();
    }
}
