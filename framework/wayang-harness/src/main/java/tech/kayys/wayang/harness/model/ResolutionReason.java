package tech.kayys.wayang.harness.model;

/**
 * Represents a resolution reason.
 *
 * <p>Its components capture `strategy`, `explanation`.</p>
 *
 * @param strategy the strategy
 * @param explanation the explanation
 */


public record ResolutionReason(
        String strategy,
        String explanation
) {
    public static ResolutionReason localFirst(String explanation) {
        return new ResolutionReason("LOCAL_FIRST", explanation);
    }

    public static ResolutionReason cloudFallback(String explanation) {
        return new ResolutionReason("CLOUD_FALLBACK", explanation);
    }

    public static ResolutionReason capabilityMatch(String explanation) {
        return new ResolutionReason("CAPABILITY_MATCH", explanation);
    }
}
