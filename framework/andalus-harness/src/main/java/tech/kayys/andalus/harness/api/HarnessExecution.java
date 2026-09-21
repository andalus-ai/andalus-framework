package tech.kayys.andalus.harness.api;

import tech.kayys.andalus.harness.lifecycle.HarnessExecutionStatus;

import java.util.concurrent.CompletableFuture;

/**
 * Handle representing an active or completed execution running inside the Harness.
 */
public interface HarnessExecution {

    String id();

    HarnessExecutionStatus status();

    CompletableFuture<HarnessResult> completion();

    void cancel();

    void suspend();

    void resume();
}
