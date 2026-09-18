package tech.kayys.wayang.spi.sandbox;

import java.util.Optional;

public interface SandboxContextAccessor {

    Optional<SandboxContext> sandboxContext();

    default Optional<String> sandboxId() {
        return sandboxContext().map(SandboxContext::sandboxId);
    }

    default Optional<String> executionId() {
        return sandboxContext().map(SandboxContext::executionId);
    }
}
