package tech.kayys.andalus.spi.sandbox.observability;

public interface SandboxHealthProvider {

    SandboxHealth check(
            String sandboxId)
            throws Exception;
}
