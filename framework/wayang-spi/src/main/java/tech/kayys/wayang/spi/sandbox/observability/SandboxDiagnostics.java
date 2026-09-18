package tech.kayys.wayang.spi.sandbox.observability;

public interface SandboxDiagnostics {

    SandboxDiagnosticsSnapshot inspect(
            String sandboxId)
            throws Exception;
}
