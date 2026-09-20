package tech.kayys.wayang.execution.portability;

import java.time.Instant;
import java.util.Objects;

/**
 * Cryptographic digest proving integrity of a portable execution package.
 */
public record PackageDigest(
    String algorithm,
    String hashValue,
    Instant computedAt
) {
    public PackageDigest {
        Objects.requireNonNull(algorithm, "algorithm cannot be null");
        Objects.requireNonNull(hashValue, "hashValue cannot be null");
        Objects.requireNonNull(computedAt, "computedAt cannot be null");
    }

    public static PackageDigest sha256(String hashValue) {
        return new PackageDigest("SHA-256", hashValue, Instant.now());
    }
}
