package tech.kayys.wayang.harness.semantics;

/**
 * Estimated resource profile and physical constraints of an operation.
 */
public record ResourceImpact(
        long estimatedMemoryBytes,
        long estimatedCpuMillis,
        boolean requiresNetwork,
        boolean requiresFilesystem
) {

    public static ResourceImpact minimal() {
        return new ResourceImpact(0L, 0L, false, false);
    }

    public static ResourceImpact standard(boolean network, boolean filesystem) {
        return new ResourceImpact(64 * 1024 * 1024L, 1000L, network, filesystem);
    }
}
