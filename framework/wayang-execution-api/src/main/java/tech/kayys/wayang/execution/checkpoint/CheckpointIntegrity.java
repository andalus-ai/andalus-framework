package tech.kayys.wayang.execution.checkpoint;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;

public record CheckpointIntegrity(String algorithm, String checksum) {
    public CheckpointIntegrity {
        Objects.requireNonNull(algorithm, "algorithm cannot be null");
        Objects.requireNonNull(checksum, "checksum cannot be null");
    }

    public static CheckpointIntegrity sha256(byte[] payload) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(payload);
            return new CheckpointIntegrity("SHA-256", HexFormat.of().formatHex(digest));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    public boolean verify(byte[] payload) {
        CheckpointIntegrity computed = sha256(payload);
        return this.checksum.equalsIgnoreCase(computed.checksum());
    }
}
