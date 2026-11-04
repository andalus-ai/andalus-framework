package tech.kayys.andalus.reasoner;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

/**
 * Default asynchronous coordinator for the framework reasoner SPI.
 *
 * <p>The supplied executor owns scheduling policy. Cancellation and deadlines
 * are checked immediately before and after provider execution. Since the
 * existing {@link Reasoner} contract has no cooperative cancellation parameter,
 * this service cannot forcibly stop a provider that is already running.</p>
 */
public final class DefaultReasoningService implements ReasoningService {

    private final ReasonerRegistry registry;
    private final Executor executor;

    public DefaultReasoningService(ReasonerRegistry registry, Executor executor) {
        this.registry = Objects.requireNonNull(registry, "registry must not be null");
        this.executor = Objects.requireNonNull(executor, "executor must not be null");
    }

    @Override
    public CompletionStage<ReasoningResult> reason(ReasoningRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        final Reasoner provider;
        try {
            provider = registry.require(request.strategy());
        } catch (RuntimeException exception) {
            return CompletableFuture.failedFuture(exception);
        }

        return CompletableFuture.supplyAsync(() -> execute(provider, request), executor);
    }

    private ReasoningResult execute(Reasoner provider, ReasoningRequest request) {
        ReasoningExecutionContext execution = request.executionContext();
        execution.checkActive();

        final ReasoningResult result;
        try {
            result = provider.reason(request.plan(), request.graphContext());
        } catch (Exception exception) {
            throw new ReasoningFailureException(
                    "Reasoner failed for strategy " + request.strategy(), exception);
        }

        execution.checkActive();
        if (result == null) {
            throw new IllegalStateException(
                    "Reasoner returned null for strategy " + request.strategy());
        }

        for (ReasoningStep step : result.steps()) {
            execution.reportStep(step);
        }
        return result;
    }
}
