package tech.kayys.andalus.harness.fabric;

/** Immutable outcome counts for one bounded worker polling pass. */
public record TaskPollResult(int claimed, int completed, int requeued,
                             int deadLettered, int lostClaims) {
    public TaskPollResult {
        if (claimed < 0 || completed < 0 || requeued < 0 || deadLettered < 0 || lostClaims < 0
                || completed + requeued + deadLettered + lostClaims > claimed) {
            throw new IllegalArgumentException("Invalid task poll counts");
        }
    }
}
