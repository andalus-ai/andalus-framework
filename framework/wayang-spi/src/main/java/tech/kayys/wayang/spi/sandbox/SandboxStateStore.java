package tech.kayys.wayang.spi.sandbox;

import java.util.List;
import java.util.Optional;

public interface SandboxStateStore {

    void save(SandboxSnapshot snapshot) throws Exception;

    Optional<SandboxSnapshot> find(String sandboxId) throws Exception;

    List<SandboxSnapshot> findActive() throws Exception;

    void delete(String sandboxId) throws Exception;
}
