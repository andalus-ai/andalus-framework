package tech.kayys.andalus.reasoner;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicBoolean;

import tech.kayys.andalus.execution.CancellationToken;
import tech.kayys.andalus.execution.ExecutionContext;

import static org.junit.jupiter.api.Assertions.*;

class ReasoningExecutionContextTest {

    @Test
    void reportsStepsAndTracksRemainingBudget() {
        List<ReasoningStep> received = new ArrayList<>();
        ReasoningExecutionContext context = context(
                CancellationToken.noop(), Duration.ofSeconds(5), 2, received::add);

        context.reportStep(ReasoningStep.of("analysis", "first", "content"));
        context.reportStep(ReasoningStep.of("analysis", "second", "content"));

        assertEquals(2, context.reportedSteps());
        assertEquals(0, context.remainingSteps());
        assertEquals(2, received.size());
    }

    @Test
    void rejectsStepBeyondConfiguredBudget() {
        ReasoningExecutionContext context = context(
                CancellationToken.noop(), Duration.ofSeconds(5), 1, step -> { });
        context.reportStep(ReasoningStep.of("analysis", "first", "content"));

        assertThrows(IllegalStateException.class,
                () -> context.reportStep(ReasoningStep.of("analysis", "second", "content")));
        assertEquals(1, context.reportedSteps());
    }

    @Test
    void observesInvocationCancellation() {
        AtomicBoolean cancelled = new AtomicBoolean();
        CancellationToken token = token(cancelled);
        ReasoningExecutionContext context = context(
                token, Duration.ofSeconds(5), 2, step -> { });
        cancelled.set(true);

        assertThrows(CancellationException.class, context::checkActive);
    }

    @Test
    void rejectsExpiredDeadline() throws InterruptedException {
        ReasoningExecutionContext context = context(
                CancellationToken.noop(), Duration.ofNanos(1), 2, step -> { });
        Thread.sleep(2);

        assertThrows(CancellationException.class, context::checkActive);
    }

    @Test
    void validatesConfiguration() {
        assertThrows(IllegalArgumentException.class,
                () -> context(CancellationToken.noop(), Duration.ZERO, 1, step -> { }));
        assertThrows(IllegalArgumentException.class,
                () -> context(CancellationToken.noop(), Duration.ofSeconds(1), 0, step -> { }));
    }

    private static ReasoningExecutionContext context(
            CancellationToken token,
            Duration timeout,
            int maxSteps,
            java.util.function.Consumer<ReasoningStep> listener) {
        ExecutionContext invocation = (ExecutionContext) Proxy.newProxyInstance(
                ExecutionContext.class.getClassLoader(),
                new Class<?>[]{ExecutionContext.class},
                (proxy, method, args) -> {
                    if ("cancellation".equals(method.getName())) {
                        return token;
                    }
                    if ("toString".equals(method.getName())) {
                        return "TestExecutionContext";
                    }
                    throw new UnsupportedOperationException(method.getName());
                });
        return new ReasoningExecutionContext(invocation, timeout, maxSteps, listener);
    }

    private static CancellationToken token(AtomicBoolean cancelled) {
        return (CancellationToken) Proxy.newProxyInstance(
                CancellationToken.class.getClassLoader(),
                new Class<?>[]{CancellationToken.class},
                (proxy, method, args) -> {
                    if ("isCancelled".equals(method.getName())) {
                        return cancelled.get();
                    }
                    if ("toString".equals(method.getName())) {
                        return "TestCancellationToken";
                    }
                    throw new UnsupportedOperationException(method.getName());
                });
    }
}
