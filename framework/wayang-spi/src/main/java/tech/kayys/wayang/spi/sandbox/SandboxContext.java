package tech.kayys.wayang.spi.sandbox;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

public interface SandboxContext {

    String sandboxId();

    String executionId();

    Optional<String> tenantId();

    default Optional<String> userId() {
        return Optional.empty();
    }

    Optional<String> agentId();

    default Optional<String> sessionId() {
        return Optional.empty();
    }

    default Optional<String> correlationId() {
        return Optional.empty();
    }

    SandboxDescriptor descriptor();

    Optional<Path> workspace();

    Optional<Path> inputDirectory();

    Optional<Path> outputDirectory();

    Instant createdAt();

    default Optional<Instant> deadline() {
        return Optional.empty();
    }

    Map<String, Object> attributes();

    default Optional<Object> attribute(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(attributes().get(name));
    }
}
