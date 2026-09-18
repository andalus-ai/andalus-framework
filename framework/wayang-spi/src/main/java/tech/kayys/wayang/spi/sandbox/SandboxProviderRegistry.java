package tech.kayys.wayang.spi.sandbox;

import java.util.List;
import java.util.Optional;

public interface SandboxProviderRegistry {

    void register(SandboxProvider provider);

    void unregister(String providerId);

    Optional<SandboxProvider> find(String providerId);

    List<SandboxProvider> findAll();
}
