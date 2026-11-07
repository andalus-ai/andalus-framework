package tech.kayys.andalus.reasoner;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import tech.kayys.andalus.execution.ExecutionContext;

/**
 * Invocation-scoped controls and progress reporting for one reasoning operation.
 *
 * <p>The invocation context remains the source of truth for cancellation and
 * lifecycle. This class adds reasoning-specific time and step limits without
 * changing the existing {@link Reasoner} SPI.</p>
 */
public final class ReasoningExecutionContext {

    private final ExecutionContext executionContext;
    private final long startedAtNanos;
    private final long timeoutNanos;
    private final int maxSteps;
    private final Consumer<ReasoningStep> stepListener;
    private final AtomicInteger reportedSteps = new AtomicInteger();

    public ReasoningExecutionContext(
            ExecutionContext executionContext,
            Duration timeout,
            int maxSteps,
            Consumer<ReasoningStep> stepListener) {
        this.executionContext = Objects.requireNonNull(
                executionContext, "executionContext must not be null");
        Objects.requireNonNull(timeout, "timeout must not be null");
        if (timeout.isZero() || timeout.isNegative()) {
            throw new IllegalArgumentException("timeout must be greater than zero");
        }
        if (maxSteps < 1) {
            throw new IllegalArgumentException("maxSteps must be at least 1");
        }
        this.startedAtNanos = System.nanoTime();
        this.timeoutNanos = timeout.toNanos();
        this.maxSteps = maxSteps;
        this.stepListener = Objects.requireNonNull(
                stepListener, "stepListener must not be null");
    }

    /** Checks invocation cancellation and the reasoning deadline. */
    public void checkActive() {
        if (executionContext.cancellation().isCancelled()) {
            throw new CancellationException("Reasoning execution was cancelled");
        }
        if (System.nanoTime() - startedAtNanos >= timeoutNanos) {
            throw new CancellationException("Reasoning execution exceeded its time limit");
        }
    }

    /**
     * Reports a completed reasoning step to the supplied listener.
     *
     * @throws CancellationException if the invocation was cancelled or timed out
     * @throws IllegalStateException if reporting would exceed the step budget
     */
    public void reportStep(ReasoningStep step) {
        Objects.requireNonNull(step, "step must not be null");
        checkActive();

        while (true) {
            int current = reportedSteps.get();
            if (current >= maxSteps) {
                throw new IllegalStateException(
                        "Reasoning step budget exhausted (maxSteps=" + maxSteps + ")");
            }
            if (reportedSteps.compareAndSet(current, current + 1)) {
                break;
            }
        }

        stepListener.accept(step);
    }

    public int reportedSteps() {
        return reportedSteps.get();
    }

    public int remainingSteps() {
        return Math.max(0, maxSteps - reportedSteps.get());
    }

    public long elapsedMillis() {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAtNanos);
    }

    public int maxSteps() {
        return maxSteps;
    }
}
