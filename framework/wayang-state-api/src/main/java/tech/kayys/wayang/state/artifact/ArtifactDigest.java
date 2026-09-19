package tech.kayys.wayang.state.artifact;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;

public record ArtifactDigest(String algorithm, String hexValue) {
    public ArtifactDigest {
        Objects.requireNonNull(algorithm, "algorithm cannot be null");
        Objects.requireNonNull(hexValue, "hexValue cannot be null");
    }

    public static ArtifactDigest sha256(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(data);
            return new ArtifactDigest("SHA-256", HexFormat.of().formatHex(hash));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    public static ArtifactDigest sha256(String hex) {
        return new ArtifactDigest("SHA-256", hex);
    }

    public String uri() {
        return "artifact://" + algorithm.toLowerCase() + "/" + hexValue;
    }
}
