package tech.kayys.wayang.execution.portability;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;

/**
 * Utility for packaging and verifying portable execution content integrity.
 */
public record ExecutionPackage(PortableExecution execution) {

    public ExecutionPackage {
        Objects.requireNonNull(execution, "execution cannot be null");
    }

    public static ExecutionPackage of(ExecutionManifest manifest, byte[] payload) {
        String hash = computeDigest(payload);
        PackageDigest digest = PackageDigest.sha256(hash);
        return new ExecutionPackage(new PortableExecution(manifest, digest, payload));
    }

    public boolean verifyIntegrity() {
        String actualHash = computeDigest(execution.payload());
        return actualHash.equalsIgnoreCase(execution.digest().hashValue());
    }

    private static String computeDigest(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(data != null ? data : new byte[0]);
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
