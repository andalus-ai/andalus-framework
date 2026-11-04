package tech.kayys.andalus.reasoner;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicBoolean;

import tech.kayys.andalus.execution.CancellationToken;
import tech.kayys.andalus.execution.ExecutionContext;
import tech.kayys.andalus.execution.GraphExecutionContext;
import tech.kayys.andalus.planner.Plan;

import static org.junit.jupiter.api.Assertions.*;

class DefaultReasoningServiceTest {

    @Test
    void resolvesAndExecutesRequestedStrategy() {
        ReasoningStep step = ReasoningStep.of("analysis", "inspect", "evidence");
        ReasoningResult expected = ReasoningResult.of("conclusion", List.of(step));
        Reasoner provider = provider(Set.of(ReasoningStrategy.REACT), expected, null);
        DefaultReasoningService service = new DefaultReasoningService(
                new ReasonerRegistry(List.of(provider)), Runnable::run);
        ReasoningExecutionContext execution = execution(CancellationToken.noop());
        ReasoningRequest request = request(ReasoningStrategy.REACT, execution);

        ReasoningResult actual = service.reason(request).toCompletableFuture().join();

        assertSame(expected, actual);
        assertEquals(1, execution.reportedSteps());
        assertEquals(0, execution.remainingSteps());
    }

    @Test
    void failsWhenNoProviderSupportsRequestedStrategy() {
        Reasoner provider = provider(Set.of(ReasoningStrategy.REACT),
                ReasoningResult.of("unused"), null);
        DefaultReasoningService service = new DefaultReasoningService(
                new ReasonerRegistry(List.of(provider)), Runnable::run);

        CompletionException error = assertThrows(CompletionException.class,
                () -> service.reason(request(
                        ReasoningStrategy.REFLEXION, execution(CancellationToken.noop())))
                        .toCompletableFuture().join());

        assertInstanceOf(IllegalArgumentException.class, error.getCause());
    }

    @Test
    void checksCancellationBeforeInvokingProvider() {
        AtomicBoolean cancelled = new AtomicBoolean(true);
        AtomicBoolean invoked = new AtomicBoolean();
        Reasoner provider = provider(Set.of(ReasoningStrategy.REACT),
                ReasoningResult.of("unused"), invoked);
        DefaultReasoningService service = new DefaultReasoningService(
                new ReasonerRegistry(List.of(provider)), Runnable::run);

        assertThrows(CompletionException.class,
                () -> service.reason(request(
                        ReasoningStrategy.REACT, execution(token(cancelled))))
                        .toCompletableFuture().join());
        assertFalse(invoked.get());
    }

    @Test
    void wrapsProviderFailuresWithStrategyContext() {
        Reasoner provider = provider(Set.of(ReasoningStrategy.REACT), null,
                null, new IllegalStateException("provider unavailable"));
        DefaultReasoningService service = new DefaultReasoningService(
                new ReasonerRegistry(List.of(provider)), Runnable::run);

        CompletionException error = assertThrows(CompletionException.class,
                () -> service.reason(request(
                        ReasoningStrategy.REACT, execution(CancellationToken.noop())))
                        .toCompletableFuture().join());

        assertInstanceOf(ReasoningFailureException.class, error.getCause());
        assertTrue(error.getCause().getMessage().contains("REACT"));
    }

    private static ReasoningRequest request(
            ReasoningStrategy strategy, ReasoningExecutionContext execution) {
        GraphExecutionContext graph = (GraphExecutionContext) Proxy.newProxyInstance(
                GraphExecutionContext.class.getClassLoader(),
                new Class<?>[]{GraphExecutionContext.class},
                (proxy, method, args) -> null);
        return new ReasoningRequest(strategy, Plan.of("test", List.of()), graph, execution);
    }

    private static ReasoningExecutionContext execution(CancellationToken token) {
        ExecutionContext invocation = (ExecutionContext) Proxy.newProxyInstance(
                ExecutionContext.class.getClassLoader(),
                new Class<?>[]{ExecutionContext.class},
                (proxy, method, args) -> {
                    if ("cancellation".equals(method.getName())) return token;
                    return null;
                });
        return new ReasoningExecutionContext(invocation, Duration.ofSeconds(5), 1, step -> { });
    }

    private static CancellationToken token(AtomicBoolean cancelled) {
        return (CancellationToken) Proxy.newProxyInstance(
                CancellationToken.class.getClassLoader(),
                new Class<?>[]{CancellationToken.class},
                (proxy, method, args) -> {
                    if ("isCancelled".equals(method.getName())) return cancelled.get();
                    return null;
                });
    }

    private static Reasoner provider(
            Set<ReasoningStrategy> strategies,
            ReasoningResult result,
            AtomicBoolean invoked) {
        return provider(strategies, result, invoked, null);
    }

    private static Reasoner provider(
            Set<ReasoningStrategy> strategies,
            ReasoningResult result,
            AtomicBoolean invoked,
            RuntimeException failure) {
        return (Reasoner) Proxy.newProxyInstance(
                Reasoner.class.getClassLoader(),
                new Class<?>[]{Reasoner.class},
                (proxy, method, args) -> {
                    if ("getSupportedStrategies".equals(method.getName())) return strategies;
                    if ("reason".equals(method.getName())) {
                        if (invoked != null) invoked.set(true);
                        if (failure != null) throw failure;
                        return result;
                    }
                    if ("toString".equals(method.getName())) return "TestReasoner";
                    if ("hashCode".equals(method.getName())) return System.identityHashCode(proxy);
                    if ("equals".equals(method.getName())) return proxy == args[0];
                    return null;
                });
    }
}
