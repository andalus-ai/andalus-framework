package tech.kayys.wayang.spi.sandbox;

import java.util.Optional;

public interface SandboxReconciler {

    Optional<Sandbox> reconnect(SandboxSnapshot snapshot) throws Exception;

    boolean supportsRecovery(SandboxSnapshot snapshot);
}
