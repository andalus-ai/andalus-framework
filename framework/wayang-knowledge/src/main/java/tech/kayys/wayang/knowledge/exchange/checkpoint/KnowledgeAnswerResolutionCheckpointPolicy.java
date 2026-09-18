package tech.kayys.wayang.knowledge.exchange.checkpoint;

/**
 * Represents a knowledge answer resolution checkpoint policy.
 *
 * <p>Its components capture `checkpoint interval entries`, `minimum compaction entries`, `retained checkpoints`, `require verification`, `require atomic install`.</p>
 *
 * @param checkpointIntervalEntries the checkpoint interval entries
 * @param minimumCompactionEntries the minimum compaction entries
 * @param retainedCheckpoints the retained checkpoints
 * @param requireVerification the require verification
 * @param requireAtomicInstall the require atomic install
 */


public record KnowledgeAnswerResolutionCheckpointPolicy(
        long checkpointIntervalEntries,
        long minimumCompactionEntries,
        int retainedCheckpoints,
        boolean requireVerification,
        boolean requireAtomicInstall
) {
    public static KnowledgeAnswerResolutionCheckpointPolicy defaults() {
        return new KnowledgeAnswerResolutionCheckpointPolicy(
                10_000,
                20_000,
                2,
                true,
                true
        );
    }

    public KnowledgeAnswerResolutionCheckpointPolicy {
        if (checkpointIntervalEntries <= 0) {
            throw new IllegalArgumentException("checkpointIntervalEntries must be > 0");
        }
        if (minimumCompactionEntries < 0) {
            throw new IllegalArgumentException("minimumCompactionEntries must be >= 0");
        }
        if (retainedCheckpoints < 1) {
            throw new IllegalArgumentException("retainedCheckpoints must be >= 1");
        }
    }
}
