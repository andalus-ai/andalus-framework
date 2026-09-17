package tech.kayys.wayang.harness.runtime;

import tech.kayys.wayang.harness.api.HarnessExecution;
import tech.kayys.wayang.harness.api.HarnessResult;
import tech.kayys.wayang.harness.lifecycle.HarnessExecutionStatus;
import tech.kayys.wayang.harness.lifecycle.HarnessLifecycle;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Default implementation of {@link HarnessExecution} coordinating lifecycle transitions and completion.
 */
public class DefaultHarnessExecution implements HarnessExecution {

    private final String id;
    private final HarnessLifecycle lifecycle;
    private final CompletableFuture<HarnessResult> completionFuture;

    public DefaultHarnessExecution(String id, HarnessLifecycle lifecycle) {
        this.id = Objects.requireNonNull(id, "id");
        this.lifecycle = Objects.requireNonNull(lifecycle, "lifecycle");
        this.completionFuture = new CompletableFuture<>();
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public HarnessExecutionStatus status() {
        return lifecycle.status();
    }

    @Override
    public CompletableFuture<HarnessResult> completion() {
        return completionFuture;
    }

    @Override
    public void cancel() {
        if (!completionFuture.isDone()) {
            if (!lifecycle.isTerminal()) {
                lifecycle.cancel();
            }
            completionFuture.complete(HarnessResult.cancelled(id));
        }
    }

    @Override
    public void suspend() {
        lifecycle.suspend();
    }

    @Override
    public void resume() {
        lifecycle.resume();
    }

    public HarnessLifecycle lifecycle() {
        return lifecycle;
    }

    public void completeSuccess(Object output) {
        if (!completionFuture.isDone()) {
            if (!lifecycle.isTerminal()) {
                lifecycle.transitionTo(HarnessExecutionStatus.COMPLETED);
            }
            completionFuture.complete(HarnessResult.success(id, output));
        }
    }

    public void completeFailure(String error) {
        if (!completionFuture.isDone()) {
            if (!lifecycle.isTerminal()) {
                lifecycle.transitionTo(HarnessExecutionStatus.FAILED);
            }
            completionFuture.complete(HarnessResult.failure(id, error));
        }
    }
}
