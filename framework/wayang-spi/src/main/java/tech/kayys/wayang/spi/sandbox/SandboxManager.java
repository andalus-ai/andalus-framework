package tech.kayys.wayang.spi.sandbox;

import java.util.List;
import java.util.Optional;

public interface SandboxManager {

    Sandbox create(SandboxRequest request) throws Exception;

    Optional<Sandbox> find(String sandboxId);

    List<Sandbox> list();

    void destroy(String sandboxId) throws Exception;
}
