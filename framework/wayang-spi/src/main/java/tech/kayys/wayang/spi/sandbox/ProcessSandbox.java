package tech.kayys.wayang.spi.sandbox;

import java.util.concurrent.CompletableFuture;

public interface ProcessSandbox extends Sandbox {

    default SandboxFilesystem filesystem() {
        return SandboxFilesystem.empty();
    }

    CompletableFuture<ProcessExecutionResult> execute(
            ProcessExecutionRequest request);
}
