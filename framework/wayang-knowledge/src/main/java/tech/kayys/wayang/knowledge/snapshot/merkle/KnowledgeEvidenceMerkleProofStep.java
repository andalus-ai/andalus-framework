package tech.kayys.wayang.knowledge.snapshot.merkle;

/**
 * Represents a knowledge evidence merkle proof step.
 *
 * <p>Its components capture `sibling hash`, `direction`.</p>
 *
 * @param siblingHash the sibling hash
 * @param direction the direction
 */


public record KnowledgeEvidenceMerkleProofStep(
        String siblingHash,
        Direction direction
) {
    /**
     * Enumerates the direction values used by the Wayang framework.
     */

    public enum Direction {
        LEFT,
        RIGHT
    }
}
