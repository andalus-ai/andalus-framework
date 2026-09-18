package tech.kayys.wayang.spi.sandbox;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

public interface SandboxContext {

    String sandboxId();

    String executionId();

    Optional<String> tenantId();

    Optional<String> agentId();

    SandboxDescriptor descriptor();

    Optional<Path> workspace();

    Optional<Path> inputDirectory();

    Optional<Path> outputDirectory();

    Instant createdAt();

    Map<String, Object> attributes();
}
