package tech.kayys.wayang.harness.api;

import tech.kayys.wayang.harness.lifecycle.HarnessExecutionStatus;

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
