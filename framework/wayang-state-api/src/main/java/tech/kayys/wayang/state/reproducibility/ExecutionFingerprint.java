package tech.kayys.wayang.state.reproducibility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public record ExecutionFingerprint(
        String hash,
        Map<String, String> components
) {
    public ExecutionFingerprint {
        Objects.requireNonNull(hash, "hash cannot be null");
        components = components == null ? Map.of() : Map.copyOf(components);
    }

    public static ExecutionFingerprint compute(Map<String, String> components) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            TreeMap<String, String> sorted = new TreeMap<>(components);
            for (Map.Entry<String, String> entry : sorted.entrySet()) {
                md.update((entry.getKey() + "=" + entry.getValue() + ";").getBytes());
            }
            String hash = HexFormat.of().formatHex(md.digest());
            return new ExecutionFingerprint(hash, sorted);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
