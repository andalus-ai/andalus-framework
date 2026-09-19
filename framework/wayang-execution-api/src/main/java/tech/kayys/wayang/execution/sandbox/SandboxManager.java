package tech.kayys.wayang.execution.sandbox;

import java.util.List;
import java.util.Optional;

/**
 * Manages the lifecycle of execution sandboxes across a host or worker.
 */
public interface SandboxManager {

    SandboxHandle create(SandboxRequest request);

    SandboxHandle start(SandboxId sandboxId);

    void pause(SandboxId sandboxId);

    void destroy(SandboxId sandboxId);

    Optional<SandboxHandle> find(SandboxId sandboxId);

    List<SandboxHandle> list();
}
