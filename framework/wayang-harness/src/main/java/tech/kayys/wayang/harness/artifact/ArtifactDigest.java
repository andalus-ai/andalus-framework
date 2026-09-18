package tech.kayys.wayang.harness.artifact;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;

/**
 * Content-addressable cryptographic digest for integrity verification and deduplication.
 */
public record ArtifactDigest(String algorithm, String hexValue) {
    public ArtifactDigest {
        Objects.requireNonNull(algorithm, "algorithm");
        Objects.requireNonNull(hexValue, "hexValue");
    }

    public static ArtifactDigest sha256(byte[] content) {
        Objects.requireNonNull(content, "content");
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(content);
            return new ArtifactDigest("SHA-256", HexFormat.of().formatHex(hash));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not found", e);
        }
    }

    public static ArtifactDigest of(String algorithm, String hexValue) {
        return new ArtifactDigest(algorithm, hexValue);
    }
}
