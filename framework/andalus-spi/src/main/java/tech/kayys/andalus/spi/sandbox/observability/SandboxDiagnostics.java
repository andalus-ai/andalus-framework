package tech.kayys.andalus.spi.sandbox.observability;

public interface SandboxDiagnostics {

    SandboxDiagnosticsSnapshot inspect(
            String sandboxId)
            throws Exception;
}
