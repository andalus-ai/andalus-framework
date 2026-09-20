package tech.kayys.wayang.execution.resource.graph;

import java.util.Objects;

/**
 * Immutable version identifier of a resource graph.
 */
public record ResourceGraphVersion(long sequence, String digest) {
    public ResourceGraphVersion {
        if (sequence < 0) {
            throw new IllegalArgumentException("sequence must be non-negative");
        }
        Objects.requireNonNull(digest, "digest cannot be null");
    }

    public static ResourceGraphVersion initial() {
        return new ResourceGraphVersion(1L, "sha256:init");
    }

    public ResourceGraphVersion next(String newDigest) {
        return new ResourceGraphVersion(sequence + 1, newDigest);
    }
}
