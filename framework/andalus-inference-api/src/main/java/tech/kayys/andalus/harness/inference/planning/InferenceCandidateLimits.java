package tech.kayys.andalus.harness.inference.planning;

import java.util.Map;

/**
 * Capability limits and operational boundaries of an inference candidate model.
 */
public record InferenceCandidateLimits(
        long maxContextTokens,
        long maxOutputTokens,
        boolean supportsStreaming,
        boolean supportsStructuredOutput,
        Map<String, Object> attributes
) {
    public InferenceCandidateLimits {
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static InferenceCandidateLimits standard(long maxContextTokens, long maxOutputTokens) {
        return new InferenceCandidateLimits(maxContextTokens, maxOutputTokens, true, true, Map.of());
    }

    public static InferenceCandidateLimits basic(long maxContextTokens, long maxOutputTokens) {
        return new InferenceCandidateLimits(maxContextTokens, maxOutputTokens, false, false, Map.of());
    }
}
