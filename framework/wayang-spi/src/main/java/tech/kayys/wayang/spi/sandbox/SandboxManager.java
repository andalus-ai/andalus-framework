package tech.kayys.wayang.spi.sandbox;

import java.util.List;
import java.util.Optional;

public interface SandboxManager {

    Sandbox create(SandboxRequest request) throws Exception;

    Optional<Sandbox> find(String sandboxId);

    List<Sandbox> list();

    default void start(String sandboxId) throws Exception {
        Sandbox s = find(sandboxId).orElseThrow(() -> new IllegalArgumentException("Unknown sandbox: " + sandboxId));
        s.start();
    }

    default void stop(String sandboxId) throws Exception {
        Sandbox s = find(sandboxId).orElseThrow(() -> new IllegalArgumentException("Unknown sandbox: " + sandboxId));
        s.stop();
    }

    void destroy(String sandboxId) throws Exception;
}
