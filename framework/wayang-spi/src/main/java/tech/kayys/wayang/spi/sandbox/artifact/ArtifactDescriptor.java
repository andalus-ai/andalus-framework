package tech.kayys.wayang.spi.sandbox.artifact;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

public record ArtifactDescriptor(
        String artifactId,
        String executionId,
        String tenantId,
        String sandboxId,
        String name,
        String mediaType,
        long size,
        String sha256,
        Instant createdAt,
        Map<String, Object> attributes
) {

    public ArtifactDescriptor {
        if (artifactId == null || artifactId.isBlank()) {
            throw new IllegalArgumentException("artifactId must not be blank");
        }
        if (executionId == null || executionId.isBlank()) {
            throw new IllegalArgumentException("executionId must not be blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (size < 0) {
            throw new IllegalArgumentException("size must not be negative");
        }
        if (sha256 == null || sha256.isBlank()) {
            throw new IllegalArgumentException("sha256 must not be blank");
        }
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public Optional<String> tenant() {
        return optional(tenantId);
    }

    public Optional<String> sandbox() {
        return optional(sandboxId);
    }

    private static Optional<String> optional(String value) {
        return value == null || value.isBlank() ? Optional.empty() : Optional.of(value);
    }
}
