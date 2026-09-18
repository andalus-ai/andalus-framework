package tech.kayys.wayang.spi.sandbox.observability;

public interface SandboxHealthProvider {

    SandboxHealth check(
            String sandboxId)
            throws Exception;
}
