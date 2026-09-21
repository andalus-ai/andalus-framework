package tech.kayys.andalus.execution.mobility.state;

import java.util.Objects;

/**
 * Portable reference to an externalized or content-addressed state object.
 */
public record StateReference(
        String id,
        String type,
        String digest,
        long size,
        String location
) {
    public StateReference {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(digest, "digest cannot be null");
        if (size < 0) {
            throw new IllegalArgumentException("size cannot be negative");
        }
    }

    public static StateReference of(String id, String type, String digest, long size, String location) {
        return new StateReference(id, type, digest, size, location);
    }
}
