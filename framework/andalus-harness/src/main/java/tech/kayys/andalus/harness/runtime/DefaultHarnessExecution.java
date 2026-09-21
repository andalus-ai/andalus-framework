package tech.kayys.andalus.harness.runtime;

import tech.kayys.andalus.harness.api.HarnessExecution;
import tech.kayys.andalus.harness.api.HarnessResult;
import tech.kayys.andalus.harness.lifecycle.HarnessExecutionStatus;
import tech.kayys.andalus.harness.lifecycle.HarnessLifecycle;

import tech.kayys.andalus.harness.execution.lifecycle.DurableExecutionControl;
import tech.kayys.andalus.harness.execution.state.ExecutionId;
import tech.kayys.andalus.harness.scheduling.cancellation.CancellationMode;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Default implementation of {@link HarnessExecution} coordinating lifecycle transitions and completion.
 */
public class DefaultHarnessExecution implements HarnessExecution {

    private final String id;
    private final HarnessLifecycle lifecycle;
    private final DurableExecutionControl durableControl;
    private final CompletableFuture<HarnessResult> completionFuture;

    public DefaultHarnessExecution(String id, HarnessLifecycle lifecycle) {
        this(id, lifecycle, null);
    }

    public DefaultHarnessExecution(String id, HarnessLifecycle lifecycle, DurableExecutionControl durableControl) {
        this.id = Objects.requireNonNull(id, "id");
        this.lifecycle = Objects.requireNonNull(lifecycle, "lifecycle");
        this.durableControl = durableControl;
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
            if (durableControl != null) {
                durableControl.cancel(ExecutionId.of(id), CancellationMode.IMMEDIATE, "Canceled via HarnessExecution");
            }
            if (!lifecycle.isTerminal()) {
                lifecycle.cancel();
            }
            completionFuture.complete(HarnessResult.cancelled(id));
        }
    }

    @Override
    public void suspend() {
        if (durableControl != null) {
            durableControl.suspend(ExecutionId.of(id));
        }
        lifecycle.suspend();
    }

    @Override
    public void resume() {
        if (durableControl != null) {
            durableControl.resume(ExecutionId.of(id));
        }
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
