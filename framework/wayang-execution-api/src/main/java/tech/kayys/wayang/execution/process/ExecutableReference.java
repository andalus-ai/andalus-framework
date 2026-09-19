package tech.kayys.wayang.execution.process;

import java.util.Objects;
import java.util.Optional;

/**
 * Declares an executable binary by name and optional path or integrity checksum.
 */
public record ExecutableReference(
        String name,
        Optional<String> expectedPath,
        Optional<String> sha256Checksum
) {

    public ExecutableReference {
        Objects.requireNonNull(name, "Executable name cannot be null");
        name = name.trim();
        expectedPath = expectedPath != null ? expectedPath : Optional.empty();
        sha256Checksum = sha256Checksum != null ? sha256Checksum : Optional.empty();
    }

    public static ExecutableReference of(String name) {
        return new ExecutableReference(name, Optional.empty(), Optional.empty());
    }

    public static ExecutableReference ofPath(String name, String path) {
        return new ExecutableReference(name, Optional.of(path), Optional.empty());
    }
}
