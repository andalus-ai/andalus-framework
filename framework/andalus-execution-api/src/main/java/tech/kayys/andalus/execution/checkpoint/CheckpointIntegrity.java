package tech.kayys.andalus.execution.checkpoint;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;

public record CheckpointIntegrity(String algorithm, String checksum) {
    public CheckpointIntegrity {
        if (algorithm == null || algorithm.isBlank()) {
            throw new IllegalArgumentException("algorithm must not be blank");
        }
        if (checksum == null || checksum.isBlank()) {
            throw new IllegalArgumentException("checksum must not be blank");
        }
        if (!"SHA-256".equalsIgnoreCase(algorithm)) {
            throw new IllegalArgumentException("Unsupported checkpoint integrity algorithm: " + algorithm);
        }
        try {
            if (HexFormat.of().parseHex(checksum).length != 32) {
                throw new IllegalArgumentException("SHA-256 checksum must contain 32 bytes");
            }
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid SHA-256 checksum", ex);
        }
    }

    public static CheckpointIntegrity sha256(byte[] payload) {
        Objects.requireNonNull(payload, "payload cannot be null");
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(payload);
            return new CheckpointIntegrity("SHA-256", HexFormat.of().formatHex(digest));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    public boolean verify(byte[] payload) {
        Objects.requireNonNull(payload, "payload cannot be null");
        byte[] expected = HexFormat.of().parseHex(checksum);
        byte[] actual = sha256(payload).checksum().getBytes(StandardCharsets.US_ASCII);
        byte[] expectedHex = HexFormat.of().formatHex(expected).getBytes(StandardCharsets.US_ASCII);
        return MessageDigest.isEqual(expectedHex, actual);
    }
}
