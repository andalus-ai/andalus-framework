package tech.kayys.andalus.observability;

public record TokenUsage(
        long inputTokens,
        long outputTokens,
        long cachedInputTokens,
        long reasoningTokens
) {

    public long totalTokens() {
        return inputTokens
                + outputTokens
                + cachedInputTokens
                + reasoningTokens;
    }
}
