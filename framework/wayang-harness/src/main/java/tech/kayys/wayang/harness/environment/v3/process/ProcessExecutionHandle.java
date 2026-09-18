package tech.kayys.wayang.harness.environment.v3.process;

import java.util.concurrent.CompletableFuture;

/**
 * Handle to a running or completed spawned process.
 */
public interface ProcessExecutionHandle {

    long pid();

    boolean isAlive();

    CompletableFuture<Integer> onExit();

    void destroy();
}
