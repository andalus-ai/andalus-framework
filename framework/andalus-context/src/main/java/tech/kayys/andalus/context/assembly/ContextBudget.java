package tech.kayys.andalus.context.assembly;

/**
 * Explicit token budget allocation for session context assembly.
 */
public record ContextBudget(
        long inputTokens,
        long outputTokens,
        long retrievalTokens,
        long toolObservationTokens) {

    public static ContextBudget of(long inputTokens, long outputTokens, long retrievalTokens, long toolObservationTokens) {
        return new ContextBudget(inputTokens, outputTokens, retrievalTokens, toolObservationTokens);
    }

    public static ContextBudget standard() {
        return new ContextBudget(8000L, 2000L, 4000L, 2000L);
    }

    public long totalBudget() {
        return inputTokens + outputTokens + retrievalTokens + toolObservationTokens;
    }
}
